package com.nft.platform.service;

import com.nft.platform.challengeservice.api.dto.response.AdditionalRewardsResponseDto;
import com.nft.platform.challengeservice.api.event.KafkaChallengeCompletedEvent;
import com.nft.platform.repository.ProfileWalletRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeRewardService {

    private final ProfileWalletRepository profileWalletRepository;

    @Transactional
    public void handleRewards(KafkaChallengeCompletedEvent event) {
        log.info("Will handle challenge rewards");
        for (AdditionalRewardsResponseDto rewardResponseDto : event.getRewards()) {
            switch (rewardResponseDto.getAwardType()) {

                case COINS:
                    profileWalletRepository.updateProfileWalletCoinBalance(
                        event.getUserId(),
                        event.getCelebrityId(),
                        rewardResponseDto.getQuantity()
                    );
                break;

                case SUBSCRIBE:
                    profileWalletRepository.updateProfileWalletSubscription(
                        event.getUserId(),
                        event.getCelebrityId(),
                        true
                    );
                break;

                case FORTUNE_WHEEL:
                    profileWalletRepository.updateProfileWalletWheelBalance(
                        event.getUserId(),
                        event.getCelebrityId(),
                        rewardResponseDto.getQuantity()
                    );
                break;

            }
        }
    }
}
