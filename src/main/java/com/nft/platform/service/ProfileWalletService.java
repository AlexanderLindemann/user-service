package com.nft.platform.service;

import com.nft.platform.domain.Period;
import com.nft.platform.domain.PoeSettings;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.dto.request.ProfileWalletPeriodUpdateDto;
import com.nft.platform.enums.Poe;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.PoeSettingsRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.util.RLockKeys;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileWalletService {

    private final SecurityUtil securityUtil;
    private final PeriodRepository periodRepository;
    private final PoeSettingsRepository poeSettingsRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final SyncService syncService;

    @Transactional
    public boolean updateProfileWalletOnPeriodIfNeeded(ProfileWalletPeriodUpdateDto requestDto) {
        UUID keycloakUserId = securityUtil.getCurrentUserId();
        String lockKey = RLockKeys.createVoteUpdateKey(keycloakUserId, requestDto.getCelebrityId());
        RLock rLock = syncService.getNamedLock(lockKey);
        return syncService.doSynchronized(rLock).run(() -> {
            Optional<Period> currentPeriodO = periodRepository.findFirst1ByOrderByStartTimeDesc();
            if (currentPeriodO.isEmpty()) {
                throw new RestException("Period does not exists", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Period currentPeriod = currentPeriodO.get();
            ProfileWallet profileWallet = profileWalletRepository
                    .findByKeycloakUserIdAndCelebrityId(keycloakUserId, requestDto.getCelebrityId())
                    .orElseThrow(() ->
                            new ItemNotFoundException(ProfileWallet.class,
                                    "keycloakUserId: " + keycloakUserId + " celebrityId: " + requestDto.getCelebrityId())
                    );
            if (profileWallet.getPeriod() != null && currentPeriod.getId().equals(profileWallet.getPeriod().getId())) {
                log.info("ProfileWallet updated for period = {}", currentPeriod);
                return false;
            }
            int freeAmountVoteOnPeriod = poeSettingsRepository.findByName(Poe.VOTE.getName())
                    .orElseThrow(() -> new ItemNotFoundException(PoeSettings.class, "poeName: " + Poe.VOTE.getName()))
                    .getFreeAmountOnPeriod();
            profileWallet.setVoteBalance(profileWallet.getVoteBalance() + freeAmountVoteOnPeriod);
            profileWallet.setPeriod(currentPeriod);
            profileWalletRepository.save(profileWallet);
            return true;
        });
    }

}
