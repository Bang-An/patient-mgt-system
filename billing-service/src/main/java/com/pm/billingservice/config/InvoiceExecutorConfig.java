package com.pm.billingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class InvoiceExecutorConfig {
    @Bean(name = "invoiceExecutor")
    public ThreadPoolTaskExecutor invoiceExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setThreadNamePrefix("invoice-exec-");
        exec.setCorePoolSize(5);
        exec.setMaxPoolSize(10);
        exec.setQueueCapacity(1000);
        exec.setAwaitTerminationSeconds(60);
        exec.setWaitForTasksToCompleteOnShutdown(true);
        exec.initialize();
        return exec;
    }
}
