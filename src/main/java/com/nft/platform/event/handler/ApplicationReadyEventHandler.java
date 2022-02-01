package com.nft.platform.event.handler;

import com.nft.platform.properties.PeriodProperties;
import com.nft.platform.service.PeriodService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationReadyEventHandler implements ApplicationListener<ApplicationReadyEvent> {

    private final PeriodService periodService;
    private final PeriodProperties periodProperties;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Start onApplicationEvent");
        CronExpression cronTrigger = CronExpression.parse(periodProperties.getCronExpression());
        LocalDateTime periodStartTime = LocalDateTime.now();
        LocalDateTime periodEndTime = cronTrigger.next(LocalDateTime.now());
        periodService.createNewPeriodIfNeeded(periodStartTime, periodEndTime);
    }

}
