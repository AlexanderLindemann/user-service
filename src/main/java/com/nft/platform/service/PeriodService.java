package com.nft.platform.service;

import com.nft.platform.common.dto.PeriodResponseDto;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.event.PeriodCreatedEvent;
import com.nft.platform.domain.Period;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.event.producer.PeriodCreatedEventProducer;
import com.nft.platform.mapper.PeriodMapper;
import com.nft.platform.properties.PeriodProperties;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.repository.PeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodService {

    private final PeriodMapper periodMapper;
    private final PeriodRepository periodRepository;
    private final PeriodProperties periodProperties;
    private final SyncService syncService;

    private final PeriodCreatedEventProducer periodCreatedEventProducer;

    @Transactional(readOnly = true)
    public Optional<PeriodResponseDto> findPeriod(PeriodStatus status) {
        log.info("Try to find Period status={}", status);
        return periodRepository.findByStatus(status)
                .map(periodMapper::toDto);
    }

    @Transactional
    public void createNewPeriodIfNeeded() {
        CronExpression cronTrigger = CronExpression.parse(periodProperties.getCronExpression());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextCronStartTime = cronTrigger.next(LocalDateTime.now());

        log.info("Try create new period");
        String lockKey = "period-update";
        RLock rLock = syncService.getNamedLock(lockKey);
        syncService.doSynchronized(rLock).run(() -> {
            Optional<Period> activePeriodO = periodRepository.findByStatus(PeriodStatus.ACTIVE);
            if (activePeriodO.isEmpty()) {
                log.info("Active period is empty. Will create new active and next period");
                createActivePeriod(now, nextCronStartTime);
                createNextPeriod(nextCronStartTime);
            } else {
                Period activePeriod = activePeriodO.get();
                if (isPeriodExpired(now, activePeriod)) {
                    log.info("Active period is expired.");
                    activePeriod.setStatus(PeriodStatus.COMPLETED);
                    Period nextPeriod = periodRepository.findByStatus(PeriodStatus.NEXT)
                            .orElseThrow(() -> new ResolutionException("Next period does not exist"));
                    nextPeriod.setStatus(PeriodStatus.ACTIVE);
                    nextPeriod.setEndTime(nextCronStartTime);
                    createNextPeriod(nextCronStartTime);
                    sendPeriodCreatedEvent(activePeriod, nextPeriod);
                } else {
                    log.info("Active period is not expired. Do nothing.");
                }
            }
        });
    }

    private void createActivePeriod(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Will create ACTIVE period startDate={}, endDate={}", startTime, endTime);
        Period period = Period.builder()
                .startTime(startTime)
                .endTime(endTime)
                .status(PeriodStatus.ACTIVE)
                .build();
        periodRepository.save(period);
    }

    private void createNextPeriod(LocalDateTime startTime) {
        log.info("Will create NEXT period startDate={}, endDate=null", startTime);
        Period period = Period.builder()
                .startTime(startTime)
                .status(PeriodStatus.NEXT)
                .build();
        periodRepository.save(period);
    }

    private boolean isPeriodExpired(LocalDateTime now, Period period) {
        return !now.isBefore(period.getEndTime());
    }

    private void sendPeriodCreatedEvent(Period previous, Period created) {
        PeriodCreatedEvent periodCreatedEvent = PeriodCreatedEvent.builder()
                .previousPeriod(periodMapper.toDto(previous))
                .createdPeriod(periodMapper.toDto(created))
                .eventType(EventType.PERIOD_CREATED)
                .build();
        periodCreatedEventProducer.handle(periodCreatedEvent);
    }

    public LocalDateTime getEndPeriod() {

        LocalDateTime period = periodRepository.findEndTimeByActiveStatus();

        int hour = period.getHour();

        if (hour <= 7) {
            return period.truncatedTo(ChronoUnit.DAYS).plusHours(8);
        } else if (hour <= 15) {
            return period.truncatedTo(ChronoUnit.DAYS).plusHours(16);
        } else {
            return period.truncatedTo(ChronoUnit.DAYS).plusDays(1);
        }

    }

}