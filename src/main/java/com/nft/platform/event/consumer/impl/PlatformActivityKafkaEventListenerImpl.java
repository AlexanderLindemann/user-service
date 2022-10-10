package com.nft.platform.event.consumer.impl;

import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.common.event.WheelRewardKafkaEvent;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.service.billing.BillingService;
import com.nft.platform.service.poe.PoeTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@OnKafkaConsumerEnabled
public class PlatformActivityKafkaEventListenerImpl {

    private final PoeTransactionService poeTransactionService;
    private final PoeTransactionMapper poeTransactionMapper;
    private final BillingService billingService;

    @KafkaListener(topics = "${spring.kafka.consumer.platform-activity-service.topic}")
    public void receive(WheelRewardKafkaEvent event) {
        log.info("Platform-activity service event received: {}", event);
        PoeTransactionRequestDto poeTransactionRequestDto = poeTransactionMapper.toRequestDto(event);
        PoeTransactionResponseDto responseDto = poeTransactionService.process(poeTransactionRequestDto);
        billingService.handleWheelRewards(event, responseDto);
    }

}