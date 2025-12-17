package com.pm.billingservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
public class Invoice {

    @Id
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    @NotNull
    @Column(name = "period_start")
    private LocalDate periodStart;

    @NotNull
    @Column(name = "period_end")
    private LocalDate periodEnd;

    @NotNull
    @Column(name = "subtotal_cents")
    private long subtotalCents;

    @NotNull
    @Column(name = "proration_cents")
    private long prorationCents;

    @NotNull
    @Column(name = "discount_cents")
    private long discountCents;

    @NotNull
    @Column(name = "total_cents")
    private long totalCents;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @NotNull
    @Column(name = "created_at")
    private LocalDate createdAt;

    @NotNull
    @Column(name = "due_at")
    private LocalDate dueAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public long getSubtotalCents() {
        return subtotalCents;
    }

    public void setSubtotalCents(long subtotalCents) {
        this.subtotalCents = subtotalCents;
    }

    public long getProrationCents() {
        return prorationCents;
    }

    public void setProrationCents(long prorationCents) {
        this.prorationCents = prorationCents;
    }

    public long getDiscountCents() {
        return discountCents;
    }

    public void setDiscountCents(long discountCents) {
        this.discountCents = discountCents;
    }

    public long getTotalCents() {
        return totalCents;
    }

    public void setTotalCents(long totalCents) {
        this.totalCents = totalCents;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDate dueAt) {
        this.dueAt = dueAt;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLine> lines) {
        this.lines = lines;
    }

    /**
     * Convenience factory to build an invoice aggregate with its lines wired up.
     */
    public static Invoice create(BillingAccount billingAccount,
                                 LocalDate periodStart,
                                 LocalDate periodEnd,
                                 long subtotalCents,
                                 long prorationCents,
                                 long discountCents,
                                 long totalCents,
                                 LocalDate createdAt,
                                 LocalDate dueAt,
                                 Collection<InvoiceLine> invoiceLines) {
        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID());
        invoice.setBillingAccount(billingAccount);
        invoice.setPeriodStart(periodStart);
        invoice.setPeriodEnd(periodEnd);
        invoice.setSubtotalCents(subtotalCents);
        invoice.setProrationCents(prorationCents);
        invoice.setDiscountCents(discountCents);
        invoice.setTotalCents(totalCents);
        invoice.setStatus(InvoiceStatus.DUE);
        invoice.setCreatedAt(createdAt);
        invoice.setDueAt(dueAt);

        if (invoiceLines != null) {
            for (InvoiceLine line : invoiceLines) {
                if (line.getId() == null) {
                    line.setId(UUID.randomUUID());
                }
                line.setInvoice(invoice);
                invoice.getLines().add(line);
            }
        }
        return invoice;
    }
}
