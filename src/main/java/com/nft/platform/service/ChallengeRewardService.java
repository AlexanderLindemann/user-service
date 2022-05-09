package com.nft.platform.service;

import com.nft.platform.challengeservice.api.dto.AbstractRewardDto;
import com.nft.platform.challengeservice.api.dto.enums.AwardType;
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
        int coinsAward = event.getRewards().stream().filter(r -> r.getAwardType() == AwardType.COINS)
                .mapToInt(AbstractRewardDto::getQuantity)
                .sum();
        log.info("Got coinsAward={}", coinsAward);
        if (coinsAward != 0) {
            profileWalletRepository.updateProfileWalletCoinBalance(event.getUserId(), event.getCelebrityId(), coinsAward);
        }
    }
}
