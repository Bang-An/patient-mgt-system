package com.pm.billingservice.service;

import com.pm.billingservice.model.*;
import com.pm.billingservice.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BillingRepositoryFacade {
    private final BillingAccountRepository billingAccountRepository;
    private final BillingAccountChangeRepository billingAccountChangeRepository;
    private final DiscountRepository discountRepository;
    private final PlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;


    public BillingRepositoryFacade(BillingAccountRepository billingAccountRepository,
                                   BillingAccountChangeRepository billingAccountChangeRepository,
                                   DiscountRepository discountRepository,
                                   PlanRepository planRepository,
                                   InvoiceRepository invoiceRepository,
                                   InvoiceLineRepository invoiceLineRepository) {
        this.billingAccountRepository = billingAccountRepository;
        this.billingAccountChangeRepository = billingAccountChangeRepository;
        this.discountRepository = discountRepository;
        this.planRepository = planRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
    }

    public boolean billingAccountExistsByPatientId(String patientId) {
        return billingAccountRepository.existsByPatientId(patientId);
    }

    public Optional<BillingAccount> findBillingAccountById(java.util.UUID billingAccountId) {
        return billingAccountRepository.findById(billingAccountId);
    }

    public BillingAccount createBillingAccount(BillingAccount billingAccount) {
        return billingAccountRepository.save(billingAccount);
    }

    public BillingAccount saveBillingAccount(BillingAccount billingAccount) {
        return billingAccountRepository.save(billingAccount);
    }

    public boolean invoiceExistsByBillingAccountIdAndPeriodStartAndPeriodEnd(UUID billingAccountId, LocalDate periodStart, LocalDate periodEnd) {
        return invoiceRepository.existsByBillingAccountIdAndPeriodStartAndPeriodEnd(billingAccountId, periodStart, periodEnd);
    }
    public Optional<Plan> findPlanByCode(PlanCode planCode) {
        return planRepository.findById(planCode);
    }

    public Optional<Discount> findDiscountByCode(DiscountCode discountCode) {
        return discountRepository.findById(discountCode);
    }

    public BillingAccountChange saveChange(BillingAccountChange change) {
        return billingAccountChangeRepository.save(change);
    }

    public List<BillingAccountChange> findChangesForPeriod(UUID billingAccountId, LocalDate start, LocalDate end) {
        return billingAccountChangeRepository.findByBillingAccountIdAndEffectiveAtBetweenOrderByEffectiveAt(
                billingAccountId, start, end);
    }

    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public InvoiceLine saveInvoiceLine(InvoiceLine invoiceLine) {
        return invoiceLineRepository.save(invoiceLine);
    }

}
