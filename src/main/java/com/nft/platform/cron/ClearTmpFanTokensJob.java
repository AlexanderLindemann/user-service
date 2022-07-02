package com.nft.platform.cron;

import com.nft.platform.service.FanTokenDistributionTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClearTmpFanTokensJob {

    @Value("${nft.token-management.enable}")
    private boolean tokenManagementEnable;

    @Value("${nft.tmpfantokens.olderThanDays}")
    private int olderThanDays;

    private final FanTokenDistributionTransactionService service;

//    @SchedulerLock(name = "clear-tmp-fantokens-lock", lockAtLeastFor = "PT10S", lockAtMostFor = "PT30S")
//    @Scheduled(cron = "${nft.tmpfantokens.cronExpression}", zone = "UTC")
    public void scheduleClearFanTokensDistributionsOlderThan() {
        log.info("ClearTmpFanTokensJob started, tokenManagementEnable = {}", tokenManagementEnable);
        if (tokenManagementEnable) {
            service.clearOlderThanDays(olderThanDays);
        }
    }
}
