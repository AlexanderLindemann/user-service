package com.nft.platform.event.handler;

import com.nft.platform.service.PeriodService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationReadyEventHandler implements ApplicationListener<ApplicationReadyEvent> {

    private final PeriodService periodService;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Start onApplicationEvent");
        periodService.createNewPeriodIfNeeded();
    }

}
