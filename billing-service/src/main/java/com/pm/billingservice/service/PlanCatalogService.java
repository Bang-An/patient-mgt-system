package com.pm.billingservice.service;

import com.pm.billingservice.exception.*;
import com.pm.billingservice.model.Discount;
import com.pm.billingservice.model.DiscountCode;
import com.pm.billingservice.model.Plan;
import com.pm.billingservice.model.PlanCode;
import org.springframework.stereotype.Service;

// 1. is plan valid and active
// 2. is discount valid
// 3. is plan discountable
// 4. is discount compatible with plan

@Service
public class PlanCatalogService {
    private final BillingRepositoryFacade billingRepositoryFacade;

    public PlanCatalogService(BillingRepositoryFacade billingRepositoryFacade) {
        this.billingRepositoryFacade = billingRepositoryFacade;
    }

    public boolean existsByPatientId(String patientId) {
        return billingRepositoryFacade.billingAccountExistsByPatientId(patientId);
    }

    public Plan loadPlan(PlanCode planCode) {
        Plan plan = billingRepositoryFacade.findPlanByCode(planCode)
                .orElseThrow(() -> new PlanNotFoundException("Plan code " + planCode + " not found"));
        if (!plan.isActive()) {
            throw new PlanInactiveException("Plan code " + planCode + " is inactive");
        }
        return plan;
    }

    public Discount loadDiscount(DiscountCode discountCode) {
        Discount discount = billingRepositoryFacade.findDiscountByCode(discountCode)
                .orElseThrow(() -> new DiscountNotFoundException("Discount code " + discountCode + " not found"));
        if (!discount.isActive()) {
            throw new DiscountInactiveException("Discount code " + discountCode + " is inactive");
        }
        return discount;
    }

    public void assertDiscountCompatibleWithPlan(Plan plan, Discount discount) {
        if (!plan.isDiscountable() && discount.getDiscountCode() != DiscountCode.DISCOUNT0) {
            throw new PlanNotDiscountableException("Plan code " + plan.getPlanCode() + " is not discountable");
        }

        if(discount.getApplyToPlanCode() != null &&
                discount.getApplyToPlanCode() != plan.getPlanCode()) {
            throw new DiscountNotAllowedException(
                    "Discount code " + discount.getDiscountCode() + " is excluded from plan code " + plan.getPlanCode());
        }
    }

    public void validatePlanAndDiscount(PlanCode planCode, DiscountCode discountCode) {
        Plan plan = loadPlan(planCode);
        Discount discount = loadDiscount(discountCode);
        assertDiscountCompatibleWithPlan(plan, discount);
    }

}
