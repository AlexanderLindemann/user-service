package com.nft.platform.event.consumer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.event.LikeAddedEvent;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.event.consumer.KafkaEventListener;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.service.poe.PoeTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@OnKafkaConsumerEnabled
public class FeedKafkaEventListenerImpl implements KafkaEventListener {

    private final ObjectMapper objectMapper;
    private final PoeTransactionService poeTransactionService;
    private final PoeTransactionMapper poeTransactionMapper;

    @Override
    @KafkaListener(topics = "${spring.kafka.consumer.feed-service.topic}")
    public void receive(String event) {
        log.trace("Feed event received: {}", event);
        try {
            LikeAddedEvent likeAddedEvent = objectMapper.readValue(event, LikeAddedEvent.class);
            if (likeAddedEvent.getEventType() != null && likeAddedEvent.getEventType() == EventType.LIKE_ADDED) {
                PoeTransactionRequestDto poeTransactionRequestDto = poeTransactionMapper.toRequestDto(likeAddedEvent);
                poeTransactionService.createPoeTransaction(poeTransactionRequestDto);
            } else {
                log.info("Skip event: {}", likeAddedEvent);
            }
        } catch (Exception e) {
            log.error("ERROR: ", e);
        }
    }
}
