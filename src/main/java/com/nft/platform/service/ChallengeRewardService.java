package com.nft.platform.service;

import com.nft.platform.challengeservice.api.dto.response.AdditionalRewardsResponseDto;
import com.nft.platform.challengeservice.api.event.KafkaChallengeCompletedEvent;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.exception.BadRequestException;
import com.nft.platform.repository.ProfileWalletRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.nft.platform.common.enums.ActivityType.CHALLENGE;
import static com.nft.platform.event.producer.impl.IncomeHistoryTransactionEventProducerImpl.formRewardTransactionEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeRewardService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProfileWalletRepository profileWalletRepository;

    @Transactional
    public void handleRewards(KafkaChallengeCompletedEvent event, PoeTransactionResponseDto responseDto) {
        for (AdditionalRewardsResponseDto rewardResponseDto : event.getRewards()) {

            switch (rewardResponseDto.getAwardType()) {
                case COINS:
                    if (profileWalletRepository.updateProfileWalletCoinBalance(
                        event.getUserId(),
                        event.getCelebrityId(),
                        rewardResponseDto.getQuantity()
                    ) == 0) {
                        throw new BadRequestException(ProfileWallet.class, "Update ProfileWallet coin balance.", "Something went wrong with updating.");
                    } else {
                        applicationEventPublisher.publishEvent(
                            formRewardTransactionEvent(
                                responseDto, rewardResponseDto.getQuantity(), 0, 0, 0, null, null, false, CHALLENGE
                            )
                        );
                    }
                break;

                case SUBSCRIBE:
                    if (profileWalletRepository.updateProfileWalletSubscription(
                        event.getUserId(),
                        event.getCelebrityId(),
                        true
                    ) == 0
                    ) {
                        throw new BadRequestException(ProfileWallet.class, "Update ProfileWallet gold status.", "Something went wrong with updating.");
                    } else {
                        applicationEventPublisher.publishEvent(
                            formRewardTransactionEvent(
                                responseDto, 0, 0, 0, 0, null, null, true, CHALLENGE
                            )
                        );
                    }
                break;

                case FORTUNE_WHEEL:
                    if (profileWalletRepository.updateProfileWalletWheelBalance(
                        event.getUserId(),
                        event.getCelebrityId(),
                        rewardResponseDto.getQuantity()
                    ) == 0
                    ) {
                        throw new BadRequestException(ProfileWallet.class, "Update ProfileWallet spin balance.", "Something went wrong with updating.");
                    } else {
                        applicationEventPublisher.publishEvent(
                            formRewardTransactionEvent(
                                responseDto, 0, rewardResponseDto.getQuantity(), 0, 0, null, null, true, CHALLENGE
                            )
                        );
                    }
                break;

            }
        }
    }
}
