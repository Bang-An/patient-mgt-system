package com.pm.patientservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBillingPlanRequestDTO {
    @NotBlank(message = "Plan code is required")
    private String planCode;

    @NotBlank(message = "Discount code is required")
    private String discountCode;

    @NotBlank(message = "Cadence code is required")
    private String cadence;

    @NotBlank(message = "Currency code is required")
    private String currency;

//    Optional effetive date if we want to support future dated changes
//    @NotBlank(message = "Request date is required")
//    private String effectiveDate;

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

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
