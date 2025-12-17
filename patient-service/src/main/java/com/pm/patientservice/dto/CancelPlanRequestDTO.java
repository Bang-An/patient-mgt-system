package com.pm.patientservice.dto;

public class CancelPlanRequestDTO {
    private String billingAccountId;
    private String effectiveDate; // optional ISO-8601 yyyy-MM-dd

    public String getBillingAccountId() {
        return billingAccountId;
    }

    public void setBillingAccountId(String billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
