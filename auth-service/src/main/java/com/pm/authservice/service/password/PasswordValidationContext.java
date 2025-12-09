package com.pm.authservice.service.password;

import java.time.LocalDate;
import java.util.Objects;

public class PasswordValidationContext {
    private final String currentPassword;
    private final String name;
    private final LocalDate dateOfBirth;

    public PasswordValidationContext(String currentPassword, String name, LocalDate dateOfBirth) {
        this.currentPassword = currentPassword;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordValidationContext that = (PasswordValidationContext) o;
        return Objects.equals(currentPassword, that.currentPassword) && Objects.equals(name,
                that.name) && Objects.equals(dateOfBirth,
                that.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPassword, name, dateOfBirth);
    }
}
