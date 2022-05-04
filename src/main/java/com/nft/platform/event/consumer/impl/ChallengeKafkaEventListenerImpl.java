package com.nft.platform.event.consumer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.challengeservice.api.event.KafkaChallengeCompletedEvent;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.event.consumer.KafkaEventListener;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.service.ChallengeRewardService;
import com.nft.platform.service.poe.PoeTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@OnKafkaConsumerEnabled
public class ChallengeKafkaEventListenerImpl implements KafkaEventListener {

    private final ObjectMapper objectMapper;
    private final PoeTransactionService poeTransactionService;
    private final PoeTransactionMapper poeTransactionMapper;
    private final ChallengeRewardService challengeRewardService;

    @Override
    @KafkaListener(topics = "${spring.kafka.consumer.challenge-service.topic}")
    public void receive(String event) {
        log.info("Challenge event received: {}", event);
        try {
            KafkaChallengeCompletedEvent challengeCompletedEvent = objectMapper.readValue(event, KafkaChallengeCompletedEvent.class);
            if (challengeCompletedEvent.getEventType() != null && challengeCompletedEvent.getEventType() == EventType.CHALLENGE_COMPLETED) {
                PoeTransactionRequestDto poeTransactionRequestDto = poeTransactionMapper.toRequestDto(challengeCompletedEvent);
                poeTransactionService.createPoeTransaction(poeTransactionRequestDto);
                challengeRewardService.handleRewards(challengeCompletedEvent);
            } else {
                log.info("Skip event: {}", challengeCompletedEvent);
            }
        } catch (Exception e) {
            log.error("ERROR: ", e);
        }
    }
}
