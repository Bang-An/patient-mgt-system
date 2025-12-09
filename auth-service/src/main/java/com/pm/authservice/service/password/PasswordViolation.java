package com.pm.authservice.service.password;

import java.util.Map;

public enum PasswordViolation {
//        LENGTH_TOO_SHORT(
//                "Password length is too short",
//                "Password must have a minimum of 14 characters"),
//        LENGTH_TOO_LONG(
//                "Password length is too long",
//                "Password must have a maximum of 48 characters"),
        // Complexity rules
        MISSING_LOWERCASE_LETTER(
                "Password must contain at least one lowercase letter",
                "Add at least one lowercase letter (a–z)"),

        MISSING_UPPERCASE_LETTER(
                "Password must contain at least one uppercase letter",
                "Add at least one uppercase letter (A–Z)"),
        MISSING_DIGIT(
                "Password must contain at least one digit",
                "Add at least one digit (0–9)"),
        MISSING_SPECIAL_CHARACTER(
                "Password must contain at least one special character",
                "Add at least one special character from: !@#$%^&*()-_=+[]{};:'\",.<>/?`~"),
        INVALID_SPECIAL_CHARACTER(
                "Password contains unsupported special characters",
                "Use only these special characters: !@#$%^&*()-_=+[]{};:'\",.<>/?`~"),

        // Personal info / reuse rules
        SAME_AS_CURRENT_PASSWORD(
                "Password must not be the same as your current password",
                "Choose a password that is different from your current password"),
        CONTAINS_NAME(
                "Password must not contain your name",
                "Remove your name or any part of it from the password"),
        CONTAINS_DATE_OF_BIRTH(
                "Password must not contain your date of birth",
                "Remove your date of birth (year, month, or day) from the password");

        private final String message;
        private final String fixHint;

    PasswordViolation(String message, String fixHint) {
        this.message = message;
        this.fixHint = fixHint;
    }

    public String getMessage() {
        return message;
    }

    public String getFixHint() {
        return fixHint;
    }
}



