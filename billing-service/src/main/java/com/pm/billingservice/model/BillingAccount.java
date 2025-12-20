package com.pm.billingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "billing_account")
public class BillingAccount {
    @Id
    private UUID id;

    @NotNull
    private String patientId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "plan_code")
    private Plan plan;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "discount_code", nullable = false)
    private Discount discount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @NotNull
    private LocalDate cycleAnchor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BillingCadence billingCadence;

    @NotNull
    private String currency;

    @NotNull
    private LocalDate activatedAt;

    private LocalDate canceledAt;

    private LocalDate lastInvoicedEnd;
    @OneToMany(mappedBy = "billingAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDate getCycleAnchor() {
        return cycleAnchor;
    }

    public void setCycleAnchor(LocalDate cycleAnchor) {
        this.cycleAnchor = cycleAnchor;
    }

    public BillingCadence getBillingCadence() {
        return billingCadence;
    }

    public void setBillingCadence(BillingCadence billingCadence) {
        this.billingCadence = billingCadence;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDate activatedAt) {
        this.activatedAt = activatedAt;
    }

    public LocalDate getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDate canceledAt) {
        this.canceledAt = canceledAt;
    }

    public LocalDate getLastInvoicedEnd() {
        return lastInvoicedEnd;
    }

    public void setLastInvoicedEnd(LocalDate lastInvoicedEnd) {
        this.lastInvoicedEnd = lastInvoicedEnd;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
