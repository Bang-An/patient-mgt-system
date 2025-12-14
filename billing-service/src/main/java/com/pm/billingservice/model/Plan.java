package com.pm.billingservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Plan {
    @Id
    @Enumerated(EnumType.STRING)
    private PlanCode planCode;
    @NotNull
    private String name;
    @NotNull
    private int monthlyPriceCents;

    @NotNull
    private int annualPriceCents;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProrationPolicy prorationPolicy;

    @NotNull
    private Boolean discountable;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BillingAccount> billingAccounts = new ArrayList<>();

    public PlanCode getPlanCode() {
        return planCode;
    }

    public void setPlanCode(PlanCode planCode) {
        this.planCode = planCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMonthlyPriceCents() {
        return monthlyPriceCents;
    }

    public void setMonthlyPriceCents(int monthlyPriceCents) {
        this.monthlyPriceCents = monthlyPriceCents;
    }

    public int getAnnualPriceCents() {
        return annualPriceCents;
    }

    public void setAnnualPriceCents(int annualPriceCents) {
        this.annualPriceCents = annualPriceCents;
    }

    public ProrationPolicy getProrationPolicy() {
        return prorationPolicy;
    }

    public void setProrationPolicy(ProrationPolicy prorationPolicy) {
        this.prorationPolicy = prorationPolicy;
    }

    public Boolean getDiscountable() {
        return discountable;
    }

    public void setDiscountable(Boolean discountable) {
        this.discountable = discountable;
    }

    public List<BillingAccount> getBillingAccounts() {
        return billingAccounts;
    }

    public void setBillingAccounts(List<BillingAccount> billingAccounts) {
        this.billingAccounts = billingAccounts;
    }
}
