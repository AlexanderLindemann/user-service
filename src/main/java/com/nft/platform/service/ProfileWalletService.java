package com.nft.platform.service;

import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.domain.*;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.dto.request.*;
import com.nft.platform.dto.response.VotePriceResponseDto;
import com.nft.platform.event.FirstAppOpenOnPeriodEvent;
import com.nft.platform.event.ProfileWalletCreatedEvent;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.mapper.VotePriceMapper;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.repository.VotePriceRepository;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.util.RLockKeys;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileWalletService {

    @Value("${nft.celebrity.default-uuid}")
    private String defaultCelebrity;

    private final SecurityUtil securityUtil;
    private final PeriodRepository periodRepository;
    private final PoeRepository poeRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final VotePriceMapper votePriceMapper;
    private final VotePriceRepository votePriceRepository;
    private final SyncService syncService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public boolean updateProfileWalletOnPeriodIfNeeded() {
        UUID celebrityId = UUID.fromString(defaultCelebrity);
        UUID keycloakUserId = securityUtil.getCurrentUserId();
        String lockKey = RLockKeys.createVoteUpdateKey(keycloakUserId, celebrityId);
        RLock rLock = syncService.getNamedLock(lockKey);
        return syncService.doSynchronized(rLock).run(() -> {
            Optional<Period> currentPeriodO = periodRepository.findByStatus(PeriodStatus.ACTIVE);
            if (currentPeriodO.isEmpty()) {
                throw new RestException("Period does not exists", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Period currentPeriod = currentPeriodO.get();
            ProfileWallet profileWallet = getProfileWallet(keycloakUserId, celebrityId);
            if (profileWallet.getPeriod() != null && currentPeriod.getId().equals(profileWallet.getPeriod().getId())) {
                log.info("ProfileWallet already updated for period = {}", currentPeriod);
                return false;
            }
            Integer freeAmountVoteOnPeriod = poeRepository.findByCode(PoeAction.VOTE)
                    .orElseThrow(() -> new ItemNotFoundException(Poe.class, "code: " + PoeAction.VOTE.getActionCode()))
                    .getFreeAmountOnPeriod();
            profileWallet.setVoteBalance(profileWallet.getVoteBalance() + freeAmountVoteOnPeriod);
            profileWallet.setPeriod(currentPeriod);
            profileWalletRepository.save(profileWallet);
            publishFirstAppOpenOnPeriod(celebrityId, keycloakUserId);
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

    private void publishFirstAppOpenOnPeriod(UUID celebrityId, UUID keycloakUserId) {
        FirstAppOpenOnPeriodEvent event = FirstAppOpenOnPeriodEvent.builder()
                .celebrityId(celebrityId)
                .userId(keycloakUserId)
                .eventType(EventType.FIRST_TIME_PERIOD_APP_OPEN)
                .build();
        applicationEventPublisher.publishEvent(event);
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

    @Transactional
    public void buyVotesForCoins(ProfileWalletVotesDto requestDto) {
        UUID keycloakUserId = securityUtil.getCurrentUserId();
        ProfileWallet profileWallet = profileWalletRepository
                .findByKeycloakUserIdAndCelebrityIdForUpdate(keycloakUserId, requestDto.getCelebrityId())
                .orElseThrow(() -> new ItemNotFoundException(ProfileWallet.class,
                        "keycloakUserId=" + keycloakUserId + " celebrityId=" + requestDto.getCelebrityId())
                );
        VotePrice votePrice = votePriceRepository.findByVotes(requestDto.getVotes())
                .orElseThrow(() -> new ItemNotFoundException(VotePrice.class, "votes=" + requestDto.getVotes()));
        int coins = votePrice.getCoins();
        if (profileWallet.getCoinBalance() < coins) {
            throw new RestException("Not enough coins", HttpStatus.CONFLICT);
        }
        profileWallet.setVoteBalance(profileWallet.getVoteBalance() + requestDto.getVotes());
        profileWallet.setCoinBalance(profileWallet.getCoinBalance() - coins);
        profileWalletRepository.save(profileWallet);
    }

    @Transactional(readOnly = true)
    public List<VotePriceResponseDto> getVoteBundles() {
        return votePriceRepository.findAll().stream()
                .map(votePriceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Integer> findWheelBalance(UUID keycloakUserId, UUID celebrityId) {
        return profileWalletRepository.findWheelBalance(keycloakUserId, celebrityId);
    }

    @Transactional
    public void decrementWheelBalance(UserVoteReductionDto requestDto) {
        ProfileWallet profileWallet = profileWalletRepository
                .findByKeycloakUserIdAndCelebrityIdForUpdate(requestDto.getKeycloakUserId(), requestDto.getCelebrityId())
                .orElseThrow(() ->
                        new RestException("ProfileWaller does not exists userId=" + requestDto.getKeycloakUserId() +
                                " celebrityId=" + requestDto.getCelebrityId(), HttpStatus.CONFLICT)
                );
        int wheelBalance = profileWallet.getWheelBalance();
        if (wheelBalance < requestDto.getAmount()) {
            throw new RestException("Not enough wheel balance for decrement. userId=" + requestDto.getKeycloakUserId() +
                    " celebrityId=" + requestDto.getCelebrityId(), HttpStatus.CONFLICT);
        }
        profileWallet.setWheelBalance(wheelBalance - requestDto.getAmount());
        profileWalletRepository.save(profileWallet);
    }

    @Transactional
    public void addCoins(UserRewardIncreaseDto requestDto) {
        profileWalletRepository.updateProfileWalletCoinBalance(
                requestDto.getKeycloakUserId(), requestDto.getCelebrityId(), requestDto.getAmount());
    }

    @Transactional
    public void addVotes(UserRewardIncreaseDto requestDto) {
        profileWalletRepository.updateProfileWalletVoteBalance(
                requestDto.getKeycloakUserId(), requestDto.getCelebrityId(), requestDto.getAmount());
    }

    @Transactional
    public void addNftVotes(UserRewardIncreaseDto requestDto) {
        profileWalletRepository.updateProfileWalletNftVoteBalance(
                requestDto.getKeycloakUserId(), requestDto.getCelebrityId(), requestDto.getAmount());
    }

}
