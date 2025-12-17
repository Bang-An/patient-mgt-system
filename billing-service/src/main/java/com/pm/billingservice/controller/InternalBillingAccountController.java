package com.pm.billingservice.controller;

import com.pm.billingservice.model.DiscountCode;
import com.pm.billingservice.model.PlanCode;
import com.pm.billingservice.service.BillingAccountService;
import java.time.LocalDate;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test-only endpoints for manipulating billing accounts.
 */
@RestController
@RequestMapping("/internal/billing-accounts")
public class InternalBillingAccountController {

    private static final Logger log = LoggerFactory.getLogger(InternalBillingAccountController.class);
    private final BillingAccountService billingAccountService;

    public InternalBillingAccountController(BillingAccountService billingAccountService) {
        this.billingAccountService = billingAccountService;
    }

    @PostMapping("/{billingAccountId}/plan-change")
    public ResponseEntity<String> changePlan(@PathVariable UUID billingAccountId,
                                             @RequestBody PlanChangeRequest request) {
        log.info("Manual plan change requested for billing account {} to plan {} with discount {} effective {}",
                billingAccountId, request.planCode, request.discountCode, request.effectiveDate);
        PlanCode planCode = PlanCode.valueOf(request.planCode.toUpperCase());
        DiscountCode discountCode = request.discountCode == null || request.discountCode.isBlank()
                ? null
                : DiscountCode.valueOf(request.discountCode.toUpperCase());
        LocalDate effectiveDate = request.effectiveDate == null || request.effectiveDate.isBlank()
                ? null
                : LocalDate.parse(request.effectiveDate);

        billingAccountService.changePlan(billingAccountId, planCode, discountCode, effectiveDate);
        return ResponseEntity.ok("Plan change applied for " + billingAccountId);
    }

    public static class PlanChangeRequest {
        public String planCode;
        public String discountCode;
        public String effectiveDate; // ISO-8601 yyyy-MM-dd
    }

    @PostMapping("/{billingAccountId}/cancel")
    public ResponseEntity<String> cancelPlan(@PathVariable UUID billingAccountId,
                                             @RequestBody CancelRequest request) {
        log.info("Manual plan cancel requested for billing account {} effective {}", billingAccountId, request.effectiveDate);
        LocalDate effectiveDate = request.effectiveDate == null || request.effectiveDate.isBlank()
                ? null
                : LocalDate.parse(request.effectiveDate);
        billingAccountService.cancelPlan(billingAccountId, effectiveDate);
        return ResponseEntity.ok("Plan canceled for " + billingAccountId);
    }

    public static class CancelRequest {
        public String effectiveDate; // ISO-8601 yyyy-MM-dd
    }
}
