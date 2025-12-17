package com.pm.billingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class BillingAccountChange {

    @Id
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    private BillingAccountChangeType changeType;

    @NotNull
    @Column(name = "effective_at")
    private LocalDate effectiveAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_plan_code")
    private PlanCode oldPlanCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_plan_code")
    private PlanCode newPlanCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_discount_code")
    private DiscountCode oldDiscountCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_discount_code")
    private DiscountCode newDiscountCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_cadence")
    private BillingCadence oldCadence;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_cadence")
    private BillingCadence newCadence;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private AccountStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private AccountStatus newStatus;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BillingAccountChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(BillingAccountChangeType changeType) {
        this.changeType = changeType;
    }

    public LocalDate getEffectiveAt() {
        return effectiveAt;
    }

    public void setEffectiveAt(LocalDate effectiveAt) {
        this.effectiveAt = effectiveAt;
    }

    public PlanCode getOldPlanCode() {
        return oldPlanCode;
    }

    public void setOldPlanCode(PlanCode oldPlanCode) {
        this.oldPlanCode = oldPlanCode;
    }

    public PlanCode getNewPlanCode() {
        return newPlanCode;
    }

    public void setNewPlanCode(PlanCode newPlanCode) {
        this.newPlanCode = newPlanCode;
    }

    public DiscountCode getOldDiscountCode() {
        return oldDiscountCode;
    }

    public void setOldDiscountCode(DiscountCode oldDiscountCode) {
        this.oldDiscountCode = oldDiscountCode;
    }

    public DiscountCode getNewDiscountCode() {
        return newDiscountCode;
    }

    public void setNewDiscountCode(DiscountCode newDiscountCode) {
        this.newDiscountCode = newDiscountCode;
    }

    public BillingCadence getOldCadence() {
        return oldCadence;
    }

    public void setOldCadence(BillingCadence oldCadence) {
        this.oldCadence = oldCadence;
    }

    public BillingCadence getNewCadence() {
        return newCadence;
    }

    public void setNewCadence(BillingCadence newCadence) {
        this.newCadence = newCadence;
    }

    public AccountStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(AccountStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public AccountStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(AccountStatus newStatus) {
        this.newStatus = newStatus;
    }

    public static BillingAccountChange of(BillingAccount billingAccount,
                                          BillingAccountChangeType changeType,
                                          LocalDate effectiveAt,
                                          PlanCode oldPlan,
                                          PlanCode newPlan,
                                          DiscountCode oldDiscount,
                                          DiscountCode newDiscount,
                                          BillingCadence oldCadence,
                                          BillingCadence newCadence,
                                          AccountStatus oldStatus,
                                          AccountStatus newStatus) {
        BillingAccountChange change = new BillingAccountChange();
        change.setId(UUID.randomUUID());
        change.setBillingAccount(billingAccount);
        change.setChangeType(changeType);
        change.setEffectiveAt(effectiveAt);
        change.setOldPlanCode(oldPlan);
        change.setNewPlanCode(newPlan);
        change.setOldDiscountCode(oldDiscount);
        change.setNewDiscountCode(newDiscount);
        change.setOldCadence(oldCadence);
        change.setNewCadence(newCadence);
        change.setOldStatus(oldStatus);
        change.setNewStatus(newStatus);
        return change;
    }
}
