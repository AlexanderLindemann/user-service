package com.nft.platform.event.handler;

import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.event.FirstAppOpenOnPeriodEvent;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.service.poe.PoeTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FirstAppOpenOnPeriodEventHandler {

    private final PoeTransactionService poeTransactionService;
    private final PoeTransactionMapper poeTransactionMapper;

    @EventListener
    public void handle(FirstAppOpenOnPeriodEvent event) {
        log.info("Get event={}", event);
        PoeTransactionRequestDto poeTransactionRequestDto = poeTransactionMapper.toRequestDto(event);
        poeTransactionService.process(poeTransactionRequestDto);
    }
}
