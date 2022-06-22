package com.nft.platform.service;

import com.nft.platform.domain.RetryBlockchainTokenDistributionTransaction;
import com.nft.platform.event.RetryTokenDistributionEvent;
import com.nft.platform.repository.RetryBlockchainTokenDistributionTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RetryBlockchainTokenDistributionTransactionService {

    private final RetryBlockchainTokenDistributionTransactionRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void clearOlderThanDays(int days) {
        log.info("clearOlderThanDays started");
        LocalDateTime beforeThisDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(days);
        int deleted = repository.deleteByCreatedAtBefore(beforeThisDate);
        log.info("clearOlderThanDays finished, deleted = {}", deleted);
    }

    @Transactional
    public void retryDistributeTransactions() {
        List<RetryBlockchainTokenDistributionTransaction> retryTransactions = repository.findAll();
        for (int i = 0; i < retryTransactions.size(); i++) {
            publishRetryTokenDistributionEvent(retryTransactions.get(i));
        }
    }

    private void publishRetryTokenDistributionEvent(RetryBlockchainTokenDistributionTransaction transaction) {
        RetryTokenDistributionEvent event = RetryTokenDistributionEvent.builder()
                .transactionId(transaction.getTransactionId())
                .keycloakUserId(transaction.getKeycloakUserId())
                .amount(transaction.getLamportsAmount())
                .build();
        applicationEventPublisher.publishEvent(event);
    }
}
