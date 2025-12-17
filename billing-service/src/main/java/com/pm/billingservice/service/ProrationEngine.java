package com.pm.billingservice.service;

import com.pm.billingservice.model.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class ProrationEngine {
    private final LocalDate billingPeriodStart;
    private final LocalDate billingPeriodEnd;
    private final BillingAccount billingAccount;
    private final BillingAccountChange billingAccountChange;
    private final BillingRepositoryFacade billingRepositoryFacade;

    public InvoiceLine calculateProratedCharges() {
        long totalDays = ChronoUnit.DAYS.between(billingPeriodStart, billingPeriodEnd);
        long remainingDays = ChronoUnit.DAYS.between(billingAccountChange.getEffectiveAt(), billingPeriodEnd);

        if (totalDays <= 0 || remainingDays <= 0) {
            return emptyLine(); // No proration needed
        }

        BillingCadence cadence = billingAccount.getBillingCadence();
        int currentPlanCents = priceFor(billingAccount.getPlan(), cadence);

        if (billingAccountChange.getChangeType() == BillingAccountChangeType.PLAN_CHANGE) {
            Plan newPlan = billingRepositoryFacade.findPlanByCode(billingAccountChange.getNewPlanCode())
                    .orElseThrow(() -> new IllegalArgumentException("New plan not found"));

            int refundCents = prorate(currentPlanCents, remainingDays, totalDays, RoundingMode.FLOOR);
            int newChargeCents = prorate(priceFor(newPlan, cadence), remainingDays, totalDays, RoundingMode.CEILING);

            int netProration = newChargeCents - refundCents;
            // build proration lines from netProration
            return InvoiceLine.create("Prorated charge for plan change", netProration);
        }

        if (billingAccountChange.getChangeType() == BillingAccountChangeType.PLAN_CANCEL) {
            int refundCents = prorate(currentPlanCents, remainingDays, totalDays, RoundingMode.FLOOR);
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

