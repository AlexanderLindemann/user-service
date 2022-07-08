package com.nft.platform.event.producer.impl;

import com.nft.platform.annotation.OnKafkaProducerEnabled;
import com.nft.platform.common.event.TransactionEvent;
import com.nft.platform.event.producer.IncomeHistoryTransactionEventProducer;
import com.nft.platform.sender.KafkaEventSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@OnKafkaProducerEnabled
@RequiredArgsConstructor
public class IncomeHistoryTransactionEventProducerImpl implements IncomeHistoryTransactionEventProducer {

    private final KafkaEventSender kafkaEventSender;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransactionEvent event) {
        log.info("Try to send event={}, topic={}", event, topic);
        kafkaEventSender.send(event, topic);
    }

}