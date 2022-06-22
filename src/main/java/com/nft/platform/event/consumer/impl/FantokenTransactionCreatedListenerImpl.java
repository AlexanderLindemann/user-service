package com.nft.platform.event.consumer.impl;

import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.event.FantokenTransactionCreatedEvent;
import com.nft.platform.event.consumer.FantokenTransactionCreatedListener;
import com.nft.platform.service.FanTokenDistributionTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@OnKafkaConsumerEnabled
public class FantokenTransactionCreatedListenerImpl implements FantokenTransactionCreatedListener {

    private final FanTokenDistributionTransactionService distributeFanTokensService;

    @Override
    @KafkaListener(topics = "${spring.kafka.consumer.token-management-service.topic}")
    public void receive(FantokenTransactionCreatedEvent event) {
        log.info("Event received: {}", event);
        distributeFanTokensService.distributeFanTokens(event);
    }
}
