package com.pm.authservice.service.password.rules;

import com.pm.authservice.service.password.PasswordValidationContext;
import com.pm.authservice.service.password.PasswordViolation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class ComplexityRule implements PasswordRule{
    private static final String ALLOWED_SPECIALS = "!@#$%^&*()-_=+[]{};:'\",.<>/?`~";

    @Override
    public List<PasswordViolation> validate(String password, PasswordValidationContext context) {
        boolean hasLowercaseLetter = false;
        boolean hasUppercaseLetter = false;
        boolean hasDigit = false;
        boolean hasValidSpecialCharacter = false;
        boolean hasInvalidSpecialCharacter = false;
        List<PasswordViolation> violations = new ArrayList<>();

        for (char c : password.toCharArray()) {
            if (!hasLowercaseLetter && Character.isLowerCase(c)) {
                hasLowercaseLetter = true;
            } else if (!hasUppercaseLetter && Character.isUpperCase(c)) {
                hasUppercaseLetter = true;
            } else if (!hasDigit && Character.isDigit(c)) {
                hasDigit = true;
            } else {
                // special char case
                if (ALLOWED_SPECIALS.indexOf(c) >= 0) {
                    hasValidSpecialCharacter = true;
                } else {
                    hasInvalidSpecialCharacter = true;
                }
            }
        }

        if (!hasLowercaseLetter) {
            violations.add(PasswordViolation.MISSING_LOWERCASE_LETTER);
        }

        if (!hasUppercaseLetter) {
            violations.add(PasswordViolation.MISSING_UPPERCASE_LETTER);
        }

        if (!hasDigit) {
            violations.add(PasswordViolation.MISSING_DIGIT);
        }

        if (!hasValidSpecialCharacter) {
            violations.add(PasswordViolation.MISSING_SPECIAL_CHARACTER);
        }

        if (hasInvalidSpecialCharacter) {
            violations.add(PasswordViolation.INVALID_SPECIAL_CHARACTER);
        }

        return violations;
    }
}
