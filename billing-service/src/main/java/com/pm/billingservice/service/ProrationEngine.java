package com.pm.billingservice.service;

import com.pm.billingservice.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;



public class ProrationEngine {
    private final LocalDate billingPeriodStart;
    private final LocalDate billingPeriodEnd;
    private final BillingAccount billingAccount;
    private final BillingAccountChange billingAccountChange;
    private final BillingRepositoryFacade billingRepositoryFacade;
    private final static Logger log = LoggerFactory.getLogger(ProrationEngine.class);

    public InvoiceLine calculateProratedCharges() {
        long totalDays = ChronoUnit.DAYS.between(billingPeriodStart, billingPeriodEnd);
        long remainingDays = ChronoUnit.DAYS.between(billingAccountChange.getEffectiveAt(), billingPeriodEnd);

        if (totalDays <= 0 || remainingDays <= 0) {
            log.warn("No proration needed for account {}: totalDays={} remainingDays={}",
                    billingAccount.getId(), totalDays, remainingDays);
            return emptyLine(); // No proration needed
        }

        BillingCadence cadence = billingAccount.getBillingCadence();
        Plan currentPlan = billingRepositoryFacade.findPlanByCode(billingAccountChange.getOldPlanCode())
                .orElseThrow(() -> new IllegalArgumentException("New plan not found"));
        int currentPlanCents = priceFor(currentPlan, cadence);

        if (billingAccountChange.getChangeType() == BillingAccountChangeType.PLAN_CHANGE) {
            Plan newPlan = billingRepositoryFacade.findPlanByCode(billingAccountChange.getNewPlanCode())
                    .orElseThrow(() -> new IllegalArgumentException("New plan not found"));

            int refundCents = prorate(currentPlanCents, remainingDays, totalDays, RoundingMode.FLOOR);
            int newChargeCents = prorate(priceFor(newPlan, cadence), remainingDays, totalDays, RoundingMode.CEILING);
            int netProration = newChargeCents - refundCents;

            log.info("Proration PLAN_CHANGE account={} effective={} period={}..{} oldPlan={} newPlan={} refund={} newCharge={} net={}",
                    billingAccount.getId(),
                    billingAccountChange.getEffectiveAt(),
                    billingPeriodStart,
                    billingPeriodEnd,
                    billingAccountChange.getOldPlanCode(),
                    newPlan.getPlanCode(),
                    refundCents,
                    newChargeCents,
                    netProration);
            return InvoiceLine.create("Prorated charge for plan change", netProration);
        }

        if (billingAccountChange.getChangeType() == BillingAccountChangeType.PLAN_CANCEL) {
            int refundCents = prorate(currentPlanCents, remainingDays, totalDays, RoundingMode.FLOOR);
            log.info("Calculating proration for plan cancellation on account {} for plan {}. Refund amount: {} cents",
                    billingAccount.getId(),
                    billingAccount.getPlan().getPlanCode(),
                    refundCents);
            return InvoiceLine.create("Prorated refund for plan cancellation", -refundCents);
        }
        return emptyLine();
    }

    private int priceFor(Plan plan, BillingCadence cadence) {
        return cadence == BillingCadence.MONTHLY ? plan.getMonthlyPriceCents() : plan.getAnnualPriceCents();
    }

    private int prorate(int amountCents, long partDays, long totalDays, RoundingMode mode) {
        return BigDecimal.valueOf(amountCents)
                .multiply(BigDecimal.valueOf(partDays))
                .divide(BigDecimal.valueOf(totalDays), 0, mode)
                .intValueExact();
    }

    public InvoiceLine emptyLine() {
        return InvoiceLine.create("No proration", 0);
    }

    private ProrationEngine(Builder builder) {
        this.billingPeriodStart = builder.billingPeriodStart;
        this.billingPeriodEnd = builder.billingPeriodEnd;
        this.billingAccount = builder.billingAccount;
        this.billingAccountChange = builder.billingAccountChange;
        this.billingRepositoryFacade = builder.billingRepositoryFacade;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate billingPeriodStart;
        private LocalDate billingPeriodEnd;
        private BillingAccount billingAccount;
        private BillingAccountChange billingAccountChange;
        private BillingRepositoryFacade billingRepositoryFacade;

        public Builder billingPeriodStart(LocalDate billingPeriodStart) {
            this.billingPeriodStart = billingPeriodStart;
            return this;
        }

        public Builder billingPeriodEnd(LocalDate billingPeriodEnd) {
            this.billingPeriodEnd = billingPeriodEnd;
            return this;
        }

        public Builder billingAccount(BillingAccount billingAccount) {
            this.billingAccount = billingAccount;
            return this;
        }

        public Builder billingAccountChange(BillingAccountChange billingAccountChange) {
            this.billingAccountChange = billingAccountChange;
            return this;
        }

        public Builder billingRepositoryFacade(BillingRepositoryFacade billingRepositoryFacade) {
            this.billingRepositoryFacade = billingRepositoryFacade;
            return this;
        }

        public ProrationEngine build() {
            return new ProrationEngine(this);
        }
    }
}

