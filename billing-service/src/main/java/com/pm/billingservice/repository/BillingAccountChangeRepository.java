package com.pm.billingservice.repository;

import com.pm.billingservice.model.BillingAccountChange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingAccountChangeRepository extends JpaRepository<BillingAccountChange, UUID> {

    List<BillingAccountChange> findByBillingAccountIdAndEffectiveAtBetweenOrderByEffectiveAt(
            UUID billingAccountId, LocalDate start, LocalDate end);
}
