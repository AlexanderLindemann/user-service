package com.nft.platform.service;

import com.nft.platform.AbstractUnitTest;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.enums.RewardType;
import com.nft.platform.common.event.WheelRewardKafkaEvent;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.repository.ProfileWalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql("classpath:sql/celebrity.sql")
@Sql("classpath:sql/user_profile.sql")
@Sql("classpath:sql/profile_wallet.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ProfileWalletServiceTest extends AbstractUnitTest {

    @Autowired
    private ProfileWalletService profileWalletService;

    @Autowired
    private ProfileWalletRepository profileWalletRepository;

    private PoeTransactionResponseDto poeTransactionResponseDto;

    private WheelRewardKafkaEvent wheelRewardKafkaEvent;

    @BeforeEach
    public void setUp() {

        UUID actionId = UUID.randomUUID();
        UUID celebrityId = UUID.fromString("25839f9e-a084-482c-82b7-8defe5fb6b62");
        UUID keyCloakId = UUID.fromString("f9894b9c-3e3a-4eb5-96d2-3174921018be");

        poeTransactionResponseDto = PoeTransactionResponseDto.builder()
            .actionId(actionId)
            .celebrityId(celebrityId)
            .userId(keyCloakId)
            .periodId(UUID.randomUUID())
        .build();

        wheelRewardKafkaEvent = WheelRewardKafkaEvent.builder()
            .actionId(actionId)
            .celebrityId(celebrityId)
            .userId(keyCloakId)
            .eventType(EventType.BUNDLE_TRANSACTION_CREATED)
            .rewards(new HashMap<>() {
                {
                    put(RewardType.COINS, 50);
                    put(RewardType.VOTES, 100);
                    put(RewardType.GOLD_STATUS, 1);
                    put(RewardType.NFT_VOTES, 200);
                }
            })
            .build();

    }

    @Test
    public void mustParseIncomingWheelRewardFromKafkaAndPersistData() {

        UUID profileWalletId = UUID.fromString("62c47f7e-a74f-4d46-81c9-ecf8f60b89d3");

        profileWalletService.handleWheelReward(wheelRewardKafkaEvent, poeTransactionResponseDto);

        ProfileWallet profileWallet = profileWalletRepository
            .findById(profileWalletId)
            .orElseThrow(() -> new ItemNotFoundException(ProfileWallet.class, profileWalletId));

        assertEquals(50, profileWallet.getCoinBalance());
        assertEquals(100, profileWallet.getVoteBalance());
        assertEquals(200, profileWallet.getNftVotesBalance());
        assertTrue(profileWallet.isSubscriber());

    }

}