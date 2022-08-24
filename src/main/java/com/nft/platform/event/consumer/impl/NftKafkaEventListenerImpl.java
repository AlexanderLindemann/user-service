package com.nft.platform.event.consumer.impl;

import com.nft.platform.annotation.OnKafkaConsumerEnabled;
import com.nft.platform.common.event.NftAddedEvent;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
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
public class NftKafkaEventListenerImpl {

    private final PoeTransactionService poeTransactionService;
    private final PoeTransactionMapper poeTransactionMapper;

    @KafkaListener(topics = "${spring.kafka.consumer.nft-service.topic}")
    public void receive(NftAddedEvent event) {
        log.info("Nft service event received: {}", event);
        PoeTransactionRequestDto poeTransactionRequestDto = poeTransactionMapper.toRequestDto(event);
        poeTransactionService.createPoeTransaction(poeTransactionRequestDto);
    }
}
