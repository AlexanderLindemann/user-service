package com.nft.platform.service;

import com.nft.platform.common.enums.EventType;
import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.Period;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.request.ProfileWalletPeriodUpdateDto;
import com.nft.platform.dto.request.SubscriptionRequestDto;
import com.nft.platform.enums.PoeAction;
import com.nft.platform.event.ProfileWalletCreatedEvent;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.util.RLockKeys;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.context.ApplicationEventPublisher;
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
    private final PoeRepository poeRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final SyncService syncService;
    private final ApplicationEventPublisher applicationEventPublisher;

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
            ProfileWallet profileWallet = getProfileWallet(keycloakUserId, requestDto.getCelebrityId());
            if (profileWallet.getPeriod() != null && currentPeriod.getId().equals(profileWallet.getPeriod().getId())) {
                log.info("ProfileWallet updated for period = {}", currentPeriod);
                return false;
            }
            Integer freeAmountVoteOnPeriod = poeRepository.findByCode(PoeAction.VOTE)
                    .orElseThrow(() -> new ItemNotFoundException(Poe.class, "code: " + PoeAction.VOTE.getActionCode()))
                    .getFreeAmountOnPeriod();
            profileWallet.setVoteBalance(profileWallet.getVoteBalance() + freeAmountVoteOnPeriod);
            profileWallet.setPeriod(currentPeriod);
            profileWalletRepository.save(profileWallet);
            return true;
        });
    }

    @Transactional(readOnly = true)
    public boolean isUserSubscriber(UUID keycloakUserId, UUID celebrityId) {
        ProfileWallet profileWallet = getProfileWallet(keycloakUserId, celebrityId);
        return profileWallet.isSubscriber();
    }

    @Transactional
    public void updateSubscriptionStatus(SubscriptionRequestDto requestDto) {
        ProfileWallet profileWallet = getProfileWallet(requestDto.getKeycloakUserId(), requestDto.getCelebrityId());
        profileWallet.setSubscriber(requestDto.isSubscriber());
        profileWalletRepository.save(profileWallet);
    }

    private ProfileWallet getProfileWallet(UUID keycloakUserId, UUID celebrityId) {
        return profileWalletRepository.findByKeycloakUserIdAndCelebrityId(keycloakUserId, celebrityId)
                .orElseThrow(() -> new ItemNotFoundException(ProfileWallet.class,
                        "keycloakUserId=" + keycloakUserId + " celebrityId=" + celebrityId)
                );
    }

    public void createAndSaveProfileWallet(UserProfile userProfile, Celebrity celebrity) {
        ProfileWallet profileWallet = new ProfileWallet();
        profileWallet.setUserProfile(userProfile);
        profileWallet.setCelebrity(celebrity);
        profileWalletRepository.save(profileWallet);
        publishProfileWalletCreatedEvent(profileWallet);
    }

    private void publishProfileWalletCreatedEvent(ProfileWallet profileWallet) {
        ProfileWalletCreatedEvent event = ProfileWalletCreatedEvent.builder()
                .profileWalletId(profileWallet.getId())
                .celebrityId(profileWallet.getCelebrity().getId())
                .userId(profileWallet.getUserProfile().getKeycloakUserId())
                .eventType(EventType.PROFILE_WALLET_CREATED)
                .build();
        applicationEventPublisher.publishEvent(event);
    }
}
