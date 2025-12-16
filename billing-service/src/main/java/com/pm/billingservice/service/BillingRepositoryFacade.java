package com.pm.billingservice.service;

import com.pm.billingservice.model.*;
import com.pm.billingservice.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BillingRepositoryFacade {
    private final BillingAccountRepository billingAccountRepository;
    private final DiscountRepository discountRepository;
    private final PlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;


    public BillingRepositoryFacade(BillingAccountRepository billingAccountRepository, DiscountRepository discountRepository,
                                   PlanRepository planRepository, InvoiceRepository invoiceRepository,
                                   InvoiceLineRepository invoiceLineRepository) {
        this.billingAccountRepository = billingAccountRepository;
        this.discountRepository = discountRepository;
        this.planRepository = planRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
    }

    public boolean billingAccountExistsByPatientId(String patientId) {
        return billingAccountRepository.existsByPatientId(patientId);
    }

    public BillingAccount createBillingAccount(BillingAccount billingAccount) {
        return billingAccountRepository.save(billingAccount);
    }
    public Optional<Plan> findPlanByCode(PlanCode planCode) {
        return planRepository.findById(planCode);
    }

    public Optional<Discount> findDiscountByCode(DiscountCode discountCode) {
        return discountRepository.findById(discountCode);
    }



}
