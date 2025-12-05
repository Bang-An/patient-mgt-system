package com.pm.authservice.mapper;

import com.pm.authservice.dto.AccountCreateDTO;
import com.pm.authservice.dto.PatientRequestDTO;

import java.util.UUID;

public class AccountToPatientMapper {
    public static PatientRequestDTO toPatientRequest(UUID id, AccountCreateDTO accountCreateDTO) {
        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setId(id.toString());
        patientRequestDTO.setName(accountCreateDTO.getName());
        patientRequestDTO.setAddress(accountCreateDTO.getAddress());
        patientRequestDTO.setEmail(accountCreateDTO.getEmail());
        patientRequestDTO.setDateOfBirth(accountCreateDTO.getDateOfBirth());
        patientRequestDTO.setRegisteredDate(accountCreateDTO.getRegisteredDate());
        return patientRequestDTO;
    }
}
