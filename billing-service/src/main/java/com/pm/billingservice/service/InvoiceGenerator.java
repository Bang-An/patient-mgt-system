package com.pm.billingservice.service;

import com.pm.billingservice.exception.AccountNotEligibleToInvoiceException;
import com.pm.billingservice.exception.AccountNotFoundException;
import com.pm.billingservice.exception.PlanNotDiscountableException;
import com.pm.billingservice.model.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceGenerator {
    private final PlanCatalogService planCatalogService;
    private final BillingRepositoryFacade billingRepositoryFacade;
    private final InvoiceAndInvoiceLineService invoiceAndInvoiceLineService;
    private final static Logger log = LoggerFactory.getLogger(InvoiceGenerator.class);

    public InvoiceGenerator(PlanCatalogService planCatalogService, BillingRepositoryFacade billingRepositoryFacade,
                            InvoiceAndInvoiceLineService invoiceAndInvoiceLineService) {
        this.planCatalogService = planCatalogService;
        this.billingRepositoryFacade = billingRepositoryFacade;
        this.invoiceAndInvoiceLineService = invoiceAndInvoiceLineService;
    }

    @Transactional
    public void generateInvoice(UUID billingAccountId) {
        // is account still existing
        BillingAccount account = billingRepositoryFacade.findBillingAccountById(billingAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Billing account " + billingAccountId + " not found"));
        // is plan/discount combination still eligible
        if (!isBillingAccountEligibleToInvoice(account)) {
            log.warn("Billing account {} is not eligible to invoice. Invoice generation aborted.", billingAccountId);
            return;
        }

        if(!isPlanCancelledBeforeCurrentBillingCycle(account)) {
            log.info("Billing account {} plan was cancelled before current billing cycle. No further invoices will be generated.",
                    billingAccountId);
            return;
        }
        // is billing period due
        LocalDate billingStartDate = account.getLastInvoicedEnd() != null ?
                account.getLastInvoicedEnd() : account.getCycleAnchor();
        LocalDate billingEndDate = account.getBillingCadence() == BillingCadence.MONTHLY ? billingStartDate.plusMonths(
                1)
                : billingStartDate.plusYears(1);
        if (billingEndDate.isAfter(LocalDate.now())) {
            log.info("Billing account {} billing period {} to {} is not yet due for invoicing.",
                    billingAccountId, billingStartDate, billingEndDate);
            return;
        }
        // Idempotency guard: Check existing invoice for (billing_account_id, period_start, period_end)
        if (billingRepositoryFacade.invoiceExistsByBillingAccountIdAndPeriodStartAndPeriodEnd(
                billingAccountId, billingStartDate, billingEndDate)) {
            log.info("Invoice for billing account {} for period {} to {} already exists. Skipping invoice generation.",
                    billingAccountId, billingStartDate, billingEndDate);
            return;
        }

        // check if there were any plan changes during the billing period
        List<BillingAccountChange> changes = billingRepositoryFacade.findChangesForPeriod(
                billingAccountId, billingStartDate, billingEndDate);
        BillingAccountChange allowedChange = !changes.isEmpty() ? changes.get(0) : null;

        // if plan changed during the period, base amount is old plan price
        BillingCadence cadence = account.getBillingCadence();
        int baseAmountCents = 0;
        if (allowedChange != null && allowedChange.getChangeType() == BillingAccountChangeType.PLAN_CHANGE) {
            Plan oldPlan = billingRepositoryFacade.findPlanByCode(allowedChange.getOldPlanCode()).orElseThrow(
                    () -> new PlanNotDiscountableException("Old plan " + allowedChange.getOldPlanCode() + " not found")
            );
            baseAmountCents = cadence == BillingCadence.MONTHLY ? oldPlan.getMonthlyPriceCents()
                    : oldPlan.getAnnualPriceCents();
        } else {
            baseAmountCents = account.getBillingCadence() == BillingCadence.MONTHLY ? account.getPlan()
                    .getMonthlyPriceCents()
                    : account.getPlan().getAnnualPriceCents();
        }

        // Use ProrationEngine to calculate any proration adjustments
        InvoiceLine prorationLine;
        if (allowedChange != null) {
            ProrationEngine prorationEngine = ProrationEngine.builder()
                    .billingAccount(account)
                    .billingAccountChange(allowedChange)
                    .billingPeriodStart(billingStartDate)
                    .billingPeriodEnd(billingEndDate)
                    .billingRepositoryFacade(billingRepositoryFacade)
                    .build();
            prorationLine = prorationEngine.calculateProratedCharges();
        } else {
            prorationLine = InvoiceLine.create("No proration", 0);
        }

        // if DISCOUNT0, no discount
        // if WELCOME10 and first month, 10% off
        // if NONPROFIT50, $50 off
        int appliedDiscountCents = 0;
        DiscountCode discountCode = account.getDiscount().getDiscountCode();
        if (discountCode == DiscountCode.WELCOME10) {
            if (account.getLastInvoicedEnd() == null) {
                appliedDiscountCents = baseAmountCents * 10 / 100;
            }
        } else if (discountCode == DiscountCode.NONPROFIT50) {
            appliedDiscountCents = 5000;
        }
        // ensure discount does not exceed base amount
        appliedDiscountCents = Math.min(appliedDiscountCents, baseAmountCents);

        // generate InvoiceLine for discount
        InvoiceLine discountLine = InvoiceLine.create("Applied discount: " + discountCode.name(),
                -appliedDiscountCents);

        // generate Invoice
        List<InvoiceLine> invoiceLines = Arrays.asList(discountLine, prorationLine);
        Invoice invoice = Invoice.create(account, billingStartDate, billingEndDate,
                baseAmountCents, 0, appliedDiscountCents, baseAmountCents - appliedDiscountCents, LocalDate.now(),
                LocalDate.now().plusDays(30), invoiceLines);
        invoiceAndInvoiceLineService.persistInvoiceAndInvoiceLines(invoice, invoiceLines);

        // update billing account last invoiced end date
        account.setLastInvoicedEnd(billingEndDate);
        billingRepositoryFacade.saveBillingAccount(account);
        log.info("Generated invoice {} for billing account {} for period {} to {}.",
                invoice.getId(), billingAccountId, billingStartDate, billingEndDate);
    }

    public boolean isBillingAccountEligibleToInvoice(BillingAccount account) {
        // Plan still active, discount still active, plan and discount compatible
        try {
            planCatalogService.validatePlanAndDiscount(account.getPlan().getPlanCode(),
                    account.getDiscount().getDiscountCode());
        } catch (Exception ex) {
            throw new AccountNotEligibleToInvoiceException(
                    "Billing account " + account.getId() + " is not eligible to invoice: " + ex.getMessage());
        }
        return true;
    }

    public boolean isPlanCancelledBeforeCurrentBillingCycle(BillingAccount account) {
        return account.getCanceledAt() != null && account.getLastInvoicedEnd() != null && account.getCanceledAt()
                .isBefore(account.getLastInvoicedEnd());
    }
}
