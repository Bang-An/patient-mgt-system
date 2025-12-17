package com.pm.billingservice.service;

import com.pm.billingservice.model.Invoice;
import com.pm.billingservice.model.InvoiceLine;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceAndInvoiceLineService {
    private final BillingRepositoryFacade billingRepositoryFacade;

    public InvoiceAndInvoiceLineService(BillingRepositoryFacade billingRepositoryFacade) {
        this.billingRepositoryFacade = billingRepositoryFacade;
    }
    @Transactional
    public void persistInvoiceAndInvoiceLines (Invoice invoice, List<InvoiceLine> invoiceLines) {
        billingRepositoryFacade.saveInvoice(invoice);
        invoiceLines.forEach(billingRepositoryFacade::saveInvoiceLine);
    }
}
