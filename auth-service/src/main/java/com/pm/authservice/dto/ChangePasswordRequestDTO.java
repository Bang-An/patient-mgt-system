package com.pm.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

public class ChangePasswordRequestDTO {
    @NotBlank
    @Size(min = 14, max = 48, message = "Password must have a length between 14 to 48 characters")
    private String newPassword;
    @NotBlank
    @Size(min = 14, max = 48, message = "Password must have a length between 14 to 48 characters")
    private String newPasswordConfirm;

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }
    @AssertTrue(message = "New password and confirmation must match")
    public boolean isNewPasswordConfirmed () {
        return newPassword != null && newPassword.equals(newPasswordConfirm);
    }

}
