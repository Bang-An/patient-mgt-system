package com.pm.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccountCreateDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    @NotBlank( message = "Registered date is required")
    private String registeredDate;

    public @NotBlank(message = "Email is required") @Email(message = "Email should be a valid email address") String getEmail() {
        return email;
    }

    public void setEmail(
            @NotBlank(message = "Email is required") @Email(message = "Email should be a valid email address") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String getPassword() {
        return password;
    }

    public void setPassword(
            @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public @NotBlank(message = "Name is required") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "name is required") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Address is required") String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(message = "Address is required") String address) {
        this.address = address;
    }

    public @NotBlank(message = "DOB is required") String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@NotBlank(message = "DOB is required") String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotBlank(message = "Registration date is required") String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(@NotBlank(message = "Registration date is required") String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
