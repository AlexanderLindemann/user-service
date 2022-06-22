package com.nft.platform.event.producer.impl;

import com.nft.platform.annotation.OnKafkaProducerEnabled;
import com.nft.platform.event.TokenDistributionTransactionChangedEvent;
import com.nft.platform.event.producer.TokenDistributionTransactionChangedProducer;
import com.nft.platform.sender.KafkaEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@OnKafkaProducerEnabled
public class TokenDistributionTransactionChangedImpl implements TokenDistributionTransactionChangedProducer {

    private final KafkaEventSender kafkaEventSender;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TokenDistributionTransactionChangedEvent event) {
        log.info("Try to send event={}, topic={}", event, topic);
        kafkaEventSender.send(event, topic);
    }
}
