package com.nft.platform.event.consumer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.event.QuizCompletedEvent;
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
public class QuizKafkaEventListenerImpl implements KafkaEventListener {

    private final ObjectMapper objectMapper;
    private final PoeTransactionService poeTransactionService;
    private final PoeTransactionMapper poeTransactionMapper;

    @Override
    @KafkaListener(topics = "${spring.kafka.consumer.quiz-service.topic}")
    public void receive(String event) {
        log.info("Quiz event received: {}", event);
        try {
            QuizCompletedEvent quizCompletedEvent = objectMapper.readValue(event, QuizCompletedEvent.class);
            if (quizCompletedEvent.getEventType() != null && quizCompletedEvent.getEventType() == EventType.QUIZ_COMPLETED) {
                PoeTransactionRequestDto poeTransactionRequestDto = poeTransactionMapper.toRequestDto(quizCompletedEvent);
                poeTransactionService.createPoeTransaction(poeTransactionRequestDto);
            } else {
                log.info("Skip event: {}", quizCompletedEvent);
            }
        } catch (Exception e) {
            log.error("ERROR: ", e);
        }
    }
}
