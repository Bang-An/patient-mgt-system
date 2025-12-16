package com.pm.billingservice.repository;

import com.pm.billingservice.model.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, UUID> {
}
