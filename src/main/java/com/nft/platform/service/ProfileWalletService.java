package com.nft.platform.service;

import com.nft.platform.common.enums.BundleType;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.common.event.VoteTransactionEvent;
import com.nft.platform.domain.BundleForCoins;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.dto.request.*;
import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.Period;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.SubscriptionRequestDto;
import com.nft.platform.dto.request.UserVoteReductionDto;
import com.nft.platform.event.FirstAppOpenOnPeriodEvent;
import com.nft.platform.event.ProfileWalletCreatedEvent;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.platformactivityservice.api.dto.enums.RewardType;
import com.nft.platform.platformactivityservice.api.event.WheelRewardKafkaEvent;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.repository.BundleForCoinsRepository;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.ProfileWalletRepository;
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

import java.util.Optional;
import java.util.UUID;

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
    private final SyncService syncService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final BundleForCoinsRepository bundleForCoinsRepository;

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

    public ProfileWallet getProfileWalletForUpdate(UUID keycloakUserId, UUID celebrityId) {
        return profileWalletRepository.findByKeycloakUserIdAndCelebrityIdForUpdate(keycloakUserId, celebrityId)
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

    @Transactional(readOnly = true)
    public long findAvailableSpins(UUID keycloakUserId, UUID celebrityId) {
        ProfileWallet profileWallet = getProfileWallet(keycloakUserId, celebrityId);
        return calculateAvailableBalance(profileWallet, BundleType.WHEEL_SPIN, profileWallet.getWheelBalance());
    }

    @Transactional(readOnly = true)
    public long findAvailableVotes(UUID keycloakUserId, UUID celebrityId) {
        ProfileWallet profileWallet = getProfileWallet(keycloakUserId, celebrityId);
        return calculateAvailableBalance(profileWallet, BundleType.VOTE, profileWallet.getVoteBalance());
    }

    private long calculateAvailableBalance(ProfileWallet profileWallet, BundleType bundleType, int balance) {
        int priceOfOneLot = findPriceOfOneLot(bundleType);
        return balance + calculateAvailableBalanceForCoins(profileWallet.getCoinBalance(), priceOfOneLot);
    }

    @Transactional
    public void decrementWheelBalance(UserVoteReductionDto requestDto) {
        ProfileWallet profileWallet = getProfileWallet(requestDto.getKeycloakUserId(), requestDto.getCelebrityId());
        int wheelBalance = profileWallet.getWheelBalance();
        if (wheelBalance >= requestDto.getAmount()) {
            profileWallet.setWheelBalance(wheelBalance - requestDto.getAmount());
        } else {
            profileWallet.setWheelBalance(0);
            handleLotsForCoins(profileWallet, BundleType.WHEEL_SPIN, wheelBalance, requestDto);
        }
        profileWalletRepository.save(profileWallet);
        applicationEventPublisher.publishEvent(
            VoteTransactionEvent.builder()
                .celebrityId(requestDto.getCelebrityId())
                .userId(requestDto.getKeycloakUserId())
                .expenses(requestDto.getAmount())
                .type(BundleType.WHEEL_SPIN)
                .eventType(EventType.VOTE_CREATED)
            .build()
        );
    }

    @Transactional
    public void decrementVoteBalance(UserVoteReductionDto requestDto) {
        ProfileWallet profileWallet = getProfileWallet(requestDto.getKeycloakUserId(), requestDto.getCelebrityId());
        int voteBalance = profileWallet.getVoteBalance();
        if (voteBalance >= requestDto.getAmount()) {
            profileWallet.setVoteBalance(voteBalance - requestDto.getAmount());
        } else {
            profileWallet.setVoteBalance(0);
            handleLotsForCoins(profileWallet, BundleType.VOTE, voteBalance, requestDto);
        }
        profileWalletRepository.save(profileWallet);
        applicationEventPublisher.publishEvent(
            VoteTransactionEvent.builder()
                .celebrityId(requestDto.getCelebrityId())
                .userId(requestDto.getKeycloakUserId())
                .expenses(requestDto.getAmount())
                .type(BundleType.VOTE)
                .eventType(EventType.VOTE_CREATED)
            .build()
        );
    }

    private void handleLotsForCoins(
            ProfileWallet profileWallet,
            BundleType bundleType,
            int availableBalance,
            UserVoteReductionDto requestDto
    ) {
        int priceOfOneLot = findPriceOfOneLot(bundleType);
        long availableBalanceForCoins = calculateAvailableBalanceForCoins(profileWallet.getCoinBalance(), priceOfOneLot);
        if (availableBalance + availableBalanceForCoins < requestDto.getAmount()) {
            throw new RestException("Not enough " + bundleType + " balance for decrement. userId=" + requestDto.getKeycloakUserId() +
                    " celebrityId=" + requestDto.getCelebrityId(), HttpStatus.CONFLICT);
        }
        int lotsBoughtForCoins = requestDto.getAmount() - availableBalance;
        profileWallet.setCoinBalance(profileWallet.getCoinBalance() - (long) lotsBoughtForCoins * priceOfOneLot);
    }

    private int findPriceOfOneLot(BundleType bundleType) {
        int bundleSize = 1;
        return bundleForCoinsRepository.findByTypeAndBundleSize(bundleType, bundleSize)
                .map(BundleForCoins::getCoins)
                .orElseThrow(() ->
                        new RestException("Bundle with size=" + bundleSize + " and type=" + bundleType
                                + " does not exist", HttpStatus.CONFLICT)
                );
    }

    private long calculateAvailableBalanceForCoins(long coinBalance, int priceOfOneLot) {
        return coinBalance / priceOfOneLot;
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

    @Transactional
    public void handleWheelReward(WheelRewardKafkaEvent wheelRewardKafkaEvent) {
        if (wheelRewardKafkaEvent.getRewardType() == RewardType.NFT_VOTES) {
            profileWalletRepository.updateProfileWalletNftVoteBalance(
                wheelRewardKafkaEvent.getUserId(),
                wheelRewardKafkaEvent.getCelebrityId(),
                wheelRewardKafkaEvent.getQuantity()
            );
        }
        if (wheelRewardKafkaEvent.getRewardType() == RewardType.COINS) {
            profileWalletRepository.updateProfileWalletCoinBalance(
                wheelRewardKafkaEvent.getUserId(),
                wheelRewardKafkaEvent.getCelebrityId(),
                wheelRewardKafkaEvent.getQuantity()
            );
        }
        if (wheelRewardKafkaEvent.getRewardType() == RewardType.VOTES) {
            profileWalletRepository.updateProfileWalletVoteBalance(
                wheelRewardKafkaEvent.getUserId(),
                wheelRewardKafkaEvent.getCelebrityId(),
                wheelRewardKafkaEvent.getQuantity()
            );
        }
        if (wheelRewardKafkaEvent.getRewardType() == RewardType.GOLD_STATUS) {
            profileWalletRepository.updateProfileWalletSubscription(
                wheelRewardKafkaEvent.getUserId(),
                wheelRewardKafkaEvent.getCelebrityId(),
                true
            );
        }
    }

}