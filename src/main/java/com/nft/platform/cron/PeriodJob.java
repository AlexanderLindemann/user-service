package com.nft.platform.cron;

import com.nft.platform.service.PeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PeriodJob {

    private final PeriodService periodService;

    @SchedulerLock(name = "period-publication-lock", lockAtLeastFor = "PT10S", lockAtMostFor = "PT30S")
    @Scheduled(cron = "${nft.period.cronExpression}", zone = "UTC")
    public void createNewPeriodIfNeeded() {
        log.info("Start PeriodJob");
        periodService.createNewPeriodIfNeeded();
    }

}
