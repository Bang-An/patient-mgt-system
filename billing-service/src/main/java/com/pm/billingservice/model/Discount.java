package com.pm.billingservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Discount {
    @Id
    @Enumerated(EnumType.STRING)
    private DiscountCode  discountCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    // if discountType is PERCENTAGE, discountValue represents basis points (e.g., 1500 bps for 15%)
    // if discountType is AMOUNT, discountValue represents a fixed amount in cents
    @NotNull
    private long discountValue;

    // Null = applies to all plans
    // PlanCode = only applies to this specific plan
    @Enumerated(EnumType.STRING)
    private PlanCode applyToPlanCode;

    @NotNull
    private boolean active;

    @OneToMany(mappedBy = "discount")
    private List<BillingAccount> billingAccounts = new ArrayList<>();

    public DiscountCode getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(DiscountCode discountCode) {
        this.discountCode = discountCode;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public long getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(long discountValue) {
        this.discountValue = discountValue;
    }

    public PlanCode getApplyToPlanCode() {
        return applyToPlanCode;
    }

    public void setApplyToPlanCode(PlanCode applyToPlanCode) {
        this.applyToPlanCode = applyToPlanCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<BillingAccount> getBillingAccounts() {
        return billingAccounts;
    }

    public void setBillingAccounts(List<BillingAccount> billingAccounts) {
        this.billingAccounts = billingAccounts;
    }
}
