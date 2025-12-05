package com.pm.authservice.service;

import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class IdGenerationService {
    public UUID generateUniqueUserId() {
        return UUID.randomUUID();
    }
}
