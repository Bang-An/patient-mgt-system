package com.pm.patientservice.dto;

public class CreateBillingPlanResponseDTO {
    private String billingAccountId;
    private String patientId;
    private String planCode;
    private String discountCode; // nullable
    private String accountStatus;
    private String cadence;
    private String cycleAnchor;
    private String activatedAt;
    private String canceledAt;    // nullable
    private String lastInvoicedEnd; // nullable

    public String getBillingAccountId() {
        return billingAccountId;
    }

    public void setBillingAccountId(String billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public String getCycleAnchor() {
        return cycleAnchor;
    }

    public void setCycleAnchor(String cycleAnchor) {
        this.cycleAnchor = cycleAnchor;
    }

    public String getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }

    public String getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(String canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getLastInvoicedEnd() {
        return lastInvoicedEnd;
    }

    public void setLastInvoicedEnd(String lastInvoicedEnd) {
        this.lastInvoicedEnd = lastInvoicedEnd;
    }
}
