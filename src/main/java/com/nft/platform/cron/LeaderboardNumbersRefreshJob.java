package com.nft.platform.cron;

import com.nft.platform.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaderboardNumbersRefreshJob {

    private final LeaderboardService leaderboardService;

//    @Scheduled(cron = "*/10 * * * * *")
//    @SchedulerLock(name = "refresh-leaderboard-lock", lockAtLeastFor = "PT10S", lockAtMostFor = "PT30S")
    public void refresh() {
        try {
            Instant start = Instant.now();
            leaderboardService.refreshView();
            Instant end = Instant.now();
            if (end.toEpochMilli() - start.toEpochMilli() > 10 * 1000) {
                log.info("Leaderboard refresh view slow time. Instant start={}, end={}", start, end);
            }
        } catch (Exception exception) {
            log.error("Leaderboard refresh view error: ", exception);
        }
    }

}
