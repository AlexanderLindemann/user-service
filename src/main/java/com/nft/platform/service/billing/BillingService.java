package com.nft.platform.service.billing;

import com.nft.platform.challengeservice.api.dto.enums.AwardType;
import com.nft.platform.challengeservice.api.event.KafkaChallengeCompletedEvent;
import com.nft.platform.common.enums.RewardType;
import com.nft.platform.common.event.WheelRewardKafkaEvent;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.exception.BadRequestException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.nft.platform.event.producer.impl.IncomeHistoryTransactionEventProducerImpl.*;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final ProfileWalletRepository profileWalletRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void award(PoeTransaction transaction, Integer amount) {
        var celebrityId = transaction.getCelebrityId();
        var userId = transaction.getUserId();
        var poe = transaction.getPoe();

        var wallet = getWallet(userId, celebrityId);
        int coinsAward = CommonUtils.toPrimitive(poe.getCoinsReward());
        int pointsAward = CommonUtils.toPrimitive(poe.getPointsReward());
        if (wallet.isSubscriber()) {
            coinsAward += CommonUtils.toPrimitive(poe.getCoinsRewardSub());
            pointsAward += CommonUtils.toPrimitive(poe.getPointsRewardSub());
        }
        transaction.setPointsReward(pointsAward * amount);
        transaction.setCoinsReward(coinsAward * amount);
        if (coinsAward != 0) {
            addCoins(userId, celebrityId, coinsAward);
        }
        if (pointsAward != 0) {
            addPoints(userId, celebrityId, pointsAward);
        }
    }

    private ProfileWallet getWallet(UUID userId, UUID celebrityId) {
        return profileWalletRepository
                .findByKeycloakUserIdAndCelebrityId(userId, celebrityId)
                .orElseThrow(() -> new ItemNotFoundException(ProfileWallet.class,
                        "keycloakUserId=" + userId + " celebrityId=" + celebrityId));
    }

    /**
     * Send {@link PoeTransactionResponseDto} to Kafka queue in order to fill up database in income-history-service and update {@link ProfileWallet} entity.
     *
     * @param event       This dto comes from platform-activity-service (kafka topic);
     * @param responseDto This dto comes after processing {@link Poe} data for exact user;
     */
    @Transactional
    public void handleWheelRewards(WheelRewardKafkaEvent event, PoeTransactionResponseDto responseDto) {
        for (RewardType rewardType : event.getRewards().keySet()) {
            Integer rewardAmount = event.getRewards().get(rewardType);
            var userId = event.getUserId();
            var celebrityId = event.getCelebrityId();
            processAward(event, responseDto, rewardType, userId, celebrityId, rewardAmount);
        }
    }

    protected void processAward(WheelRewardKafkaEvent event, PoeTransactionResponseDto responseDto, RewardType type, UUID userId, UUID celebrityId, int rewardAmount) {
        switch (type) {
            case COINS:
                addCoins(userId, celebrityId, rewardAmount);
                applicationEventPublisher.publishEvent(newCoinsWheelEvent(responseDto, rewardAmount));
                break;
            case GOLD_STATUS:
                updateSubscriptionStatus(userId, celebrityId, true);
                applicationEventPublisher.publishEvent(newSubscriptionWheelEvent(responseDto, true));
                break;
            case NFT_VOTES:
                addNftVotes(userId, celebrityId, event.getRewards().get(type));
                applicationEventPublisher.publishEvent(newNftVotesWheelEvent(responseDto, rewardAmount));
                break;
            case VOTES:
                addVotes(userId, celebrityId, event.getRewards().get(type));
                applicationEventPublisher.publishEvent(newVotesWheelEvent(responseDto, rewardAmount));
                break;
        }
    }

    @Transactional
    public void handleChallengeRewards(KafkaChallengeCompletedEvent event, PoeTransactionResponseDto responseDto) {
        for (var rewardResponseDto : event.getRewards()) {
            var userId = event.getUserId();
            var celebrityId = event.getCelebrityId();
            var awardAmount = rewardResponseDto.getQuantity();
            var awardType = rewardResponseDto.getAwardType();
            processAward(responseDto, awardType, userId, celebrityId, awardAmount);
        }
    }

    protected void processAward(PoeTransactionResponseDto responseDto, AwardType type, UUID userId, UUID celebrityId, int awardAmount) {
        switch (type) {
            case COINS:
                addCoins(userId, celebrityId, awardAmount);
                applicationEventPublisher.publishEvent(newCoinsChallengeEvent(responseDto, awardAmount));
                break;

            case SUBSCRIBE:
                updateSubscriptionStatus(userId, celebrityId, true);
                applicationEventPublisher.publishEvent(newSubscriptionChallengeEvent(responseDto, true));
                break;

            case FORTUNE_WHEEL:
                addWheelBalance(userId, celebrityId, awardAmount);
                applicationEventPublisher.publishEvent(newWheelChallengeEvent(responseDto, awardAmount));
                break;

            default:
                throw new UnsupportedOperationException(String.format("Found unknown reward type %s", type));
        }
    }

    @Transactional
    public void addNftVotes(UUID userId, UUID celebrityId, int amount) {
        profileWalletRepository.updateProfileWalletNftVoteBalance(userId, celebrityId, amount);
    }

    @Transactional
    public void addVotes(UUID userId, UUID celebrityId, int amount) {
        profileWalletRepository.updateProfileWalletVoteBalance(userId, celebrityId, amount);
    }

    @Transactional
    public void addCoins(UUID userId, UUID celebrityId, int coins) {
        if (profileWalletRepository.updateProfileWalletCoinBalance(userId, celebrityId, coins) == 0) {
            throw new BadRequestException(ProfileWallet.class, "Update ProfileWallet coin balance.", "Something went wrong with updating.");
        }
    }

    public void addPoints(UUID userId, UUID celebrityId, int points) {
        profileWalletRepository.updateProfileWalletExperienceBalance(userId, celebrityId, points);
    }

    public void addWheelBalance(UUID userId, UUID celebrityId, int amount) {
        if (profileWalletRepository.updateProfileWalletWheelBalance(userId, celebrityId, amount) == 0) {
            throw new BadRequestException(ProfileWallet.class, "Update ProfileWallet spin balance.", "Something went wrong with updating.");
        }
    }

    public void updateSubscriptionStatus(UUID userId, UUID celebrityId, boolean status) {
        if (profileWalletRepository.updateProfileWalletSubscription(userId, celebrityId, status) == 0) {
            throw new BadRequestException(ProfileWallet.class, "Update ProfileWallet gold status.", "Something went wrong with updating.");
        }
    }
}
