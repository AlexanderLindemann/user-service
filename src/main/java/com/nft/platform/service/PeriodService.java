package com.nft.platform.service;

import com.nft.platform.domain.Period;
import com.nft.platform.dto.response.PeriodResponseDto;
import com.nft.platform.mapper.PeriodMapper;
import com.nft.platform.properties.PeriodProperties;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.repository.PeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodService {

    private final PeriodMapper periodMapper;
    private final PeriodRepository periodRepository;
    private final PeriodProperties periodProperties;
    private final SyncService syncService;

    @Transactional(readOnly = true)
    public Optional<PeriodResponseDto> findCurrentPeriod() {
        log.info("Try to find current Period");
        return periodRepository.findFirst1ByOrderByStartTimeDesc()
                .map(periodMapper::toDto);
    }

    @Transactional
    public void createNewPeriodIfNeeded() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusSeconds(periodProperties.getDurationSeconds());
        createNewPeriodIfNeeded(now, endTime);
    }

    @Transactional
    public void createNewPeriodIfNeeded(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Try create new period");
        String lockKey = "period-update";
        RLock rLock = syncService.getNamedLock(lockKey);
        syncService.doSynchronized(rLock).run(() -> {
            Optional<Period> lastPeriodO = periodRepository.findFirst1ByOrderByStartTimeDesc();
            if (lastPeriodO.isEmpty()) {
                log.info("Last period is empty. Will create new");
            }
            if (lastPeriodO.isEmpty() || isPeriodExpired(startTime, lastPeriodO.get())) {
                createAndSaveNewPeriod(startTime, endTime);
            } else {
                log.info("Last period is not expired");
            }
        });
    }

    private boolean isPeriodExpired(LocalDateTime now, Period period) {
        return !now.isBefore(period.getEndTime());
    }

    private void createAndSaveNewPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Will create new period startDate={}, endDate={}", startTime, endTime);
        Period period = Period.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
        periodRepository.save(period);
    }

}
