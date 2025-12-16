package com.pm.billingservice.repository;

import com.pm.billingservice.model.Plan;
import com.pm.billingservice.model.PlanCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, PlanCode> {
}
