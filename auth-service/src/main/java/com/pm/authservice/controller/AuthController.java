package com.pm.authservice.controller;

import com.pm.authservice.dto.*;
import com.pm.authservice.exception.PatientServiceBadRequestException;
import com.pm.authservice.exception.PatientServiceException;
import com.pm.authservice.mapper.AccountToPatientMapper;
import com.pm.authservice.model.User;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.IdGenerationService;
import com.pm.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
public class AuthController {

  private final AuthService authService;
  private final IdGenerationService idGenerationService;
  private final UserService userService;
  private final WebClient patientWebClient;
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);


  public AuthController(AuthService authService, IdGenerationService idGenerationService, @Qualifier("patientClient") WebClient patientWebClient, UserService userService) {
    this.authService = authService;
    this.idGenerationService = idGenerationService;
    this.patientWebClient = patientWebClient;
    this.userService = userService;
  }

  @Operation(summary = "Generate token on user login")
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(
      @RequestBody LoginRequestDTO loginRequestDTO) {

    Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

    if (tokenOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = tokenOptional.get();
    return ResponseEntity.ok(new LoginResponseDTO(token));
  }

  @Operation(summary = "Validate Token")
  @GetMapping("/validate")
  public ResponseEntity<Void> validateToken(
      @RequestHeader("Authorization") String authHeader) {

    // Authorization: Bearer <token>
    if(authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return authService.validateToken(authHeader.substring(7))
        ? ResponseEntity.ok().build()
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  // Generate UUID for new patient
  // POST request to create patient
  // persist password hash
  // send back a 201 created status code
  @Operation(summary = "Register account for uer")
  @PostMapping("/register")
  public ResponseEntity<Void> registerAccount(@RequestBody AccountCreateDTO accountCreateDTO) {
    UUID id = idGenerationService.generateUniqueUserId();
    PatientRequestDTO patientRequestDTO= AccountToPatientMapper.toPatientRequest(id, accountCreateDTO);
    try {
      patientWebClient.post()
              .uri("/patients")
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(patientRequestDTO)
              .retrieve()
              .onStatus(HttpStatusCode::is4xxClientError,
                      resp -> resp.bodyToMono(String.class)
                              .map(body -> new PatientServiceBadRequestException(body, resp.statusCode().value())))
              .onStatus(HttpStatusCode::is5xxServerError,
                      resp -> resp.bodyToMono(String.class)
                              .map(body -> new PatientServiceException(body, resp.statusCode().value())))
              .toBodilessEntity()
              .block();
    } catch (WebClientRequestException ex) {
      throw new PatientServiceException("Patient service unreachable", ex, HttpStatus.SERVICE_UNAVAILABLE.value());
    }
    String encodedPassword = authService.encodePassword(accountCreateDTO.getPassword());
    userService.save(User.of(id, accountCreateDTO.getEmail(), encodedPassword, accountCreateDTO.getRole()));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
