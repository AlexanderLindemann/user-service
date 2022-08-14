package com.nft.platform.event.consumer.impl;

import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.common.event.PreNftEvent;
import com.nft.platform.common.util.JsonUtil;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.service.poe.PoeTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@OnKafkaConsumerEnabled
public class PreNftEventKafkaListenerImpl {

    private final PoeTransactionService poeTransactionService;
    private final PoeTransactionMapper poeTransactionMapper;

    @KafkaListener(topics = {"${spring.kafka.consumer.content-service.topic}"})
    public void receive(PreNftEvent event) {
        log.info("Content service event received: {}", JsonUtil.toJsonString(event));
        PoeTransactionRequestDto poeTransactionRequestDto = poeTransactionMapper.toRequestDto(event);
        poeTransactionService.createPoeTransaction(poeTransactionRequestDto);
    }
}
