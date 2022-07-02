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
public class ClearRetryDistributionTransactionsJob {

    @Value("${nft.retrydistribute.olderThanDays}")
    private int olderThanDays;

    @Value("${nft.token-management.enable}")
    private boolean tokenManagementEnable;

    private final RetryBlockchainTokenDistributionTransactionService service;

//    @SchedulerLock(name = "clear-retry-transaction-lock", lockAtLeastFor = "PT10S", lockAtMostFor = "PT30S")
//    @Scheduled(cron = "${nft.retrydistribute.clearcronExpression}", zone = "UTC")
    public void scheduleClearRetryDistributionsOlderThan() {
        log.info("ClearRetryDistributionTransactionsJob started, tokenManagementEnable = {}", tokenManagementEnable);
        if (tokenManagementEnable) {
            service.clearOlderThanDays(olderThanDays);
        }
    }
}
