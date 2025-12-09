package com.pm.authservice.service.password;

import com.pm.authservice.service.password.rules.PasswordRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PasswordValidator {
    private final List<PasswordRule> rules;

    public PasswordValidator(List<PasswordRule> rules) {
        this.rules = rules;
    }

    public List<PasswordViolation> validate(String password, PasswordValidationContext context) {
        List<PasswordViolation> violations = new ArrayList<>();
        for (PasswordRule rule : rules) {
            violations.addAll(rule.validate(password, context));
        }
        return violations;
    }
}

