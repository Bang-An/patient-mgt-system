package com.pm.billingservice.service;

import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.model.DiscountCode;
import com.pm.billingservice.model.PlanCode;
import org.springframework.stereotype.Service;

@Service
public class BillingAccountService {
    private final BillingRepositoryFacade billingRepositoryFacade;

    public BillingAccountService(BillingRepositoryFacade billingRepositoryFacade) {
        this.billingRepositoryFacade = billingRepositoryFacade;
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

}
