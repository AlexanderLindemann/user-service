package com.nft.platform.event.consumer.impl;

import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.common.event.AuthUserAuthorizedEvent;
import com.nft.platform.common.event.AuthUserRegisteredEvent;
import com.nft.platform.common.util.JsonUtil;
import com.nft.platform.service.poe.PoeTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@OnKafkaConsumerEnabled
public class AuthServiceEventKafkaListenerImpl {
    private final PoeTransactionService poeTransactionService;

    @KafkaListener(topics = {"${spring.kafka.consumer.auth-service.topics.authorization}"})
    public void receive(AuthUserAuthorizedEvent event) {
        log.info("Auth service event received: {}", JsonUtil.toJsonString(event));
        poeTransactionService.process(event);
    }

    @KafkaListener(topics = {"${spring.kafka.consumer.auth-service.topics.registration}"})
    public void receive(AuthUserRegisteredEvent event) {
        log.info("Auth service event received: {}", JsonUtil.toJsonString(event));
    }
}
