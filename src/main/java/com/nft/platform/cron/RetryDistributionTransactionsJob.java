package com.nft.platform.cron;

import com.nft.platform.service.RetryBlockchainTokenDistributionTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetryDistributionTransactionsJob {

    @Value("${nft.token-management.enable}")
    private boolean tokenManagementEnable;

    private final RetryBlockchainTokenDistributionTransactionService service;

    @SchedulerLock(name = "retry-distribution-transaction-lock", lockAtLeastFor = "PT10S", lockAtMostFor = "PT60S")
    @Scheduled(cron = "${nft.retrydistribute.retrycronExpression}", zone = "UTC")
    public void scheduleRetryDistributions() {
        log.info("RetryDistributionTransactionsJob started, tokenManagementEnable = {}", tokenManagementEnable);
        if (tokenManagementEnable) {
            service.retryDistributeTransactions();
        }
    }
}
