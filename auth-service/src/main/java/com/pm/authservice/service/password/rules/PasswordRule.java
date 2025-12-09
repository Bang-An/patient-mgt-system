package com.pm.authservice.service.password.rules;

import com.pm.authservice.service.password.PasswordValidationContext;
import com.pm.authservice.service.password.PasswordViolation;

import java.util.List;

public interface PasswordRule {
    List<PasswordViolation> validate(String newPassword, PasswordValidationContext context);
}
