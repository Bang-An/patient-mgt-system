package com.pm.billingservice.scheduler;

import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.repository.BillingAccountRepository;
import com.pm.billingservice.service.InvoiceGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class InvoiceScheduler {
    private final BillingAccountRepository billingAccountRepository;
    private final ThreadPoolTaskExecutor invoiceExecutor;
    private final InvoiceGenerator invoiceGenerator;
    private final Logger log = LoggerFactory.getLogger(InvoiceScheduler.class);

    public InvoiceScheduler(BillingAccountRepository billingAccountRepository,
                            @Qualifier("invoiceExecutor") ThreadPoolTaskExecutor invoiceExecutor, InvoiceGenerator invoiceGenerator) {
        this.billingAccountRepository = billingAccountRepository;
        this.invoiceExecutor = invoiceExecutor;
        this.invoiceGenerator = invoiceGenerator;
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    public void generateDueInvoices() {
        // fetch due accounts, loop, call invoiceGenerator
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        PageRequest pageRequest = PageRequest.of(0, 1000);
        Page<BillingAccount> page = billingAccountRepository.findDueAccounts(today, pageRequest);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        while (true) {
            throttleIfQueueHigh(950);
            final Page<BillingAccount> currentPage = page;

            futures.add(CompletableFuture.runAsync(() -> {
                submitPageTasks(currentPage);
            }, invoiceExecutor));

            if (!page.hasNext()) {
                break;
            }

            page = billingAccountRepository.findDueAccounts(today, page.nextPageable());
        }
        // wait for all tasks in the page to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void submitPageTasks(Page<BillingAccount> page) {
        for (BillingAccount account : page.getContent()) {
            try {
                invoiceGenerator.generateInvoice(account.getId());
            } catch (Exception e) {
                // log and swallow to avoid blocking other tasks
                log.error("Error generating invoice for account: {}", account.getId(), e);
            }
        }
    }

    private void throttleIfQueueHigh(int highWatermark) {
        ThreadPoolExecutor tpe = invoiceExecutor.getThreadPoolExecutor();
        int qSize = tpe.getQueue().size();

        while (qSize >= highWatermark) {
            log.info("Throttling: queueSize={}, active={}, poolSize={}",
                    qSize, tpe.getActiveCount(), tpe.getPoolSize());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            qSize = tpe.getQueue().size();
        }
    }



}
