package com.nft.platform.service;

import com.nft.platform.challengeservice.api.dto.enums.AwardType;
import com.nft.platform.challengeservice.api.dto.response.RewardResponseDto;
import com.nft.platform.challengeservice.api.event.KafkaChallengeCompletedEvent;
import com.nft.platform.repository.ProfileWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeRewardService {

    private final ProfileWalletRepository profileWalletRepository;

    @Transactional
    public void handleRewards(KafkaChallengeCompletedEvent event) {
        log.info("Will handle challenge rewards");
        for (RewardResponseDto rewardResponseDto : event.getRewards()) {
            if (rewardResponseDto.getAwardType() == AwardType.COINS) {
                profileWalletRepository.updateProfileWalletCoinBalance(
                        event.getUserId(),
                        event.getCelebrityId(),
                        rewardResponseDto.getQuantity()
                );
            }
            if (rewardResponseDto.getAwardType() == AwardType.SUBSCRIBE) {
                profileWalletRepository.updateProfileWalletSubscription(
                        event.getUserId(),
                        event.getCelebrityId(),
                        true
                );
            }
        }
    }
}
