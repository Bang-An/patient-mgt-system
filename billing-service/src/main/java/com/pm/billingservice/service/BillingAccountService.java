package com.pm.billingservice.service;

import com.pm.billingservice.exception.AccountNotFoundException;
import com.pm.billingservice.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BillingAccountService {
    private final BillingRepositoryFacade billingRepositoryFacade;
    private final PlanCatalogService planCatalogService;

    public BillingAccountService(BillingRepositoryFacade billingRepositoryFacade,
                                 PlanCatalogService planCatalogService) {
        this.billingRepositoryFacade = billingRepositoryFacade;
        this.planCatalogService = planCatalogService;
    }

    public BillingAccount createBillingAccount(String patientId,
                                               PlanCode planCode,
                                               DiscountCode discountCode,
                                               String cadence,
                                               String currency) {
        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setId(java.util.UUID.randomUUID());
        billingAccount.setPatientId(patientId);
        billingAccount.setPlan(planCode == null ? null : billingRepositoryFacade.findPlanByCode(planCode).orElse(null));
        billingAccount.setDiscount(discountCode == null ? null : billingRepositoryFacade.findDiscountByCode(discountCode).orElse(null));
        billingAccount.setBillingCadence(com.pm.billingservice.model.BillingCadence.valueOf(cadence.toUpperCase()));
        billingAccount.setCurrency(currency);
        billingAccount.setAccountStatus(com.pm.billingservice.model.AccountStatus.ACTIVE);
        billingAccount.setCycleAnchor(java.time.LocalDate.now());
        billingAccount.setActivatedAt(java.time.LocalDate.now());
        billingAccount.setLastInvoicedEnd(null);
        return billingRepositoryFacade.createBillingAccount(billingAccount);
    }

    @Transactional
    public BillingAccount changePlan(UUID billingAccountId,
                                     PlanCode newPlanCode,
                                     DiscountCode requestedDiscountCode,
                                     LocalDate effectiveDate) {
        BillingAccount account = billingRepositoryFacade.findBillingAccountById(billingAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Billing account " + billingAccountId + " not found"));

        // effective date must be in the current billing cycle
        if (effectiveDate != null) {
            LocalDate cycleStart = account.getLastInvoicedEnd() != null ? account.getLastInvoicedEnd() : account.getCycleAnchor();
            LocalDate cycleEnd = account.getBillingCadence() == BillingCadence.MONTHLY ?
                    cycleStart.plusMonths(1) : cycleStart.plusYears(1);
            if (effectiveDate.isBefore(cycleStart) || effectiveDate.isAfter(cycleEnd)) {
                throw new IllegalArgumentException("Effective date " + effectiveDate +
                        " is outside the current billing cycle " + cycleStart + " to " + cycleEnd);
            }
        }

        Plan newPlan = planCatalogService.loadPlan(newPlanCode);

        DiscountCode discountToApply = DiscountCode.DISCOUNT0;
        Discount discountEntity = null;

        if (newPlan.isDiscountable()) {
            discountToApply = requestedDiscountCode != null ? requestedDiscountCode
                    : account.getDiscount().getDiscountCode();
            discountEntity = planCatalogService.loadDiscount(discountToApply);
            planCatalogService.validatePlanAndDiscount(newPlan, discountEntity);
        } else {
            discountEntity = planCatalogService.loadDiscount(discountToApply);
        }


        PlanCode oldPlanCode = account.getPlan().getPlanCode();
        DiscountCode oldDiscountCode = account.getDiscount().getDiscountCode();

        account.setPlan(newPlan);
        account.setDiscount(discountEntity);
        billingRepositoryFacade.saveBillingAccount(account);

        BillingAccountChange change = BillingAccountChange.of(
                account,
                BillingAccountChangeType.PLAN_CHANGE,
                effectiveDate != null ? effectiveDate : LocalDate.now(),
                oldPlanCode,
                newPlanCode,
                oldDiscountCode,
                discountToApply,
                account.getBillingCadence(),
                account.getBillingCadence(),
                account.getAccountStatus(),
                account.getAccountStatus()
        );
        billingRepositoryFacade.saveChange(change);
        return account;
    }

    @Transactional
    public BillingAccount cancelPlan(UUID billingAccountId, LocalDate effectiveDate) {
        BillingAccount account = billingRepositoryFacade.findBillingAccountById(billingAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Billing account " + billingAccountId + " not found"));

        LocalDate cancelDate = effectiveDate != null ? effectiveDate : LocalDate.now();
        AccountStatus oldStatus = account.getAccountStatus();

        account.setAccountStatus(AccountStatus.CANCELED);
        account.setCanceledAt(cancelDate);
        billingRepositoryFacade.saveBillingAccount(account);

        BillingAccountChange change = BillingAccountChange.of(
                account,
                BillingAccountChangeType.PLAN_CANCEL,
                cancelDate,
                account.getPlan().getPlanCode(),
                null,
                account.getDiscount() == null ? null : account.getDiscount().getDiscountCode(),
                null,
                account.getBillingCadence(),
                account.getBillingCadence(),
                oldStatus,
                AccountStatus.CANCELED
        );
        billingRepositoryFacade.saveChange(change);
        return account;
    }
}
