package com.pm.patientservice.controller;

import com.pm.patientservice.dto.CreateBillingPlanRequestDTO;
import com.pm.patientservice.dto.CreateBillingPlanResponseDTO;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing Patients")
public class PatientController {

    private final PatientService patientService;
    private final static Logger log = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> patients = patientService.getPatients();

        log.info("Controller: getPatient triggered");
        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    @Operation(summary = "Create a new Patient")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @Validated({Default.class, CreatePatientValidationGroup.class})
            @RequestBody PatientRequestDTO patientRequestDTO) {

        PatientResponseDTO patientResponseDTO = patientService.createPatient(
                patientRequestDTO);

        log.info("Controller: createPatient triggered");
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    // now api gateway would parse token and inject UserId,email,role in the header
    // we do not need to rely on user provide ID anymore
    // we should check header userID == id passed in as path variable
    @PutMapping("/{id}")
    @Operation(summary = "Update a new Patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id,
                                                            @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {

        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id,
                patientRequestDTO);

        log.info("Controller: updatePatient triggered");
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);

        log.info("Controller: deletePatient triggered");
        return ResponseEntity.noContent().build();
    }

    // Create billing plan for patient
    @PostMapping("/{id}/plan-enrollment")
    @Operation(summary = "Enroll or Update Patient Billing Plan")
    public ResponseEntity<CreateBillingPlanResponseDTO> enrollOrUpdateBillingPlan(
            @RequestHeader("X-User-Id") UUID userIdInHeader,
            @PathVariable UUID id,
            @RequestBody CreateBillingPlanRequestDTO requestDTO) {

        // Ensure the user is modifying their own billing plan
        if (!userIdInHeader.equals(id)) {
            log.warn("User id in header {} does not match path variable id {}", userIdInHeader, id);
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // gRPC call to billing service to create or update billing plan
        CreateBillingPlanResponseDTO billingPlanResponseDTO = patientService.createBillingAccount(
                userIdInHeader.toString(), requestDTO.getPlanCode(), requestDTO.getDiscountCode(),
                requestDTO.getCadence(), requestDTO.getCurrency());

        return ResponseEntity.ok(billingPlanResponseDTO);

    }

}
