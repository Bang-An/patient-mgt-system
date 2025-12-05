package com.pm.authservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class UserRequestDTO {
    @NotBlank(message = "Password is required")
    private UUID id;


    private String email;


    private String password;


    private String role;
}
