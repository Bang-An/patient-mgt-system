package com.pm.billingservice.controller;

import com.pm.billingservice.service.InvoiceGenerator;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test-only endpoint to manually trigger invoice generation for a billing account.
 * Avoid exposing this publicly in production.
 */
@RestController
@RequestMapping("/internal")
public class InternalInvoiceController {

    private static final Logger log = LoggerFactory.getLogger(InternalInvoiceController.class);
    private final InvoiceGenerator invoiceGenerator;

    public InternalInvoiceController(InvoiceGenerator invoiceGenerator) {
        this.invoiceGenerator = invoiceGenerator;
    }

    @PostMapping("/invoices/generate/{billingAccountId}")
    public ResponseEntity<String> generateInvoice(@PathVariable UUID billingAccountId) {
        log.info("Manual invoice generation requested for billing account {}", billingAccountId);
        invoiceGenerator.generateInvoice(billingAccountId);
        return ResponseEntity.ok("Invoice generation triggered for " + billingAccountId);
    }
}
