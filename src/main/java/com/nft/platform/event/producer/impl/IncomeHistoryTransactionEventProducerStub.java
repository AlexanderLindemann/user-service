package com.nft.platform.event.producer.impl;

import com.nft.platform.common.event.TransactionEvent;
import com.nft.platform.event.producer.IncomeHistoryTransactionEventProducer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "enabled", havingValue = "false", matchIfMissing = true)
public class IncomeHistoryTransactionEventProducerStub implements IncomeHistoryTransactionEventProducer {

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransactionEvent event) {
        log.info("Do nothing");
    }

}