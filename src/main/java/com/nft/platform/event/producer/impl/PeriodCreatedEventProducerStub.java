package com.nft.platform.event.producer.impl;

import com.nft.platform.common.event.PeriodCreatedEvent;
import com.nft.platform.event.producer.PeriodCreatedEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "enabled", havingValue = "false", matchIfMissing = true)
@Component
@Slf4j
public class PeriodCreatedEventProducerStub implements PeriodCreatedEventProducer {

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PeriodCreatedEvent event) {
        log.info("Do nothing");
    }
}
