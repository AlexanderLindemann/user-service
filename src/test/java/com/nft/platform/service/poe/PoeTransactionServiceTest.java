package com.nft.platform.service.poe;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.event.AuthUserAuthorizedEvent;
import com.nft.platform.common.event.NftAddedEvent;
import com.nft.platform.common.util.EnumUtil;
import com.nft.platform.domain.Period;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.service.UserProfileService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.nft.platform.common.enums.EventType.AUTH_ON_LOGIN;
import static org.mockito.ArgumentMatchers.any;

@Sql("classpath:sql/period.sql")
@Sql("classpath:sql/celebrity.sql")
@Sql("classpath:sql/user_profile.sql")
@Sql("classpath:sql/profile_wallet.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PoeTransactionServiceTest extends AbstractIntegrationTest {
    private static final UUID ACTIVE_PERIOD = UUID.fromString("9b1627a7-8a97-4e19-bc3d-ed0388847d7a");
    private static final String USER_LOGIN = "auth@gmail.com";
    private static final UUID USER_CELEB = UUID.fromString("d212e77f-3057-4db5-80f2-14d52d4dae35");

    @Autowired
    PoeTransactionService poeTransactionService;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    PoeRepository poeRepository;

    @Autowired
    PoeTransactionMapper poeTransactionMapper;

    @MockBean
    PeriodRepository periodRepository;

    @BeforeEach
    public void setUp() {
        Mockito.when(periodRepository.findByStatus(any(PeriodStatus.class)))
                .thenReturn(Optional.of(Period.builder()
                        .id(ACTIVE_PERIOD)
                        .build()));
    }

//    @Test
//    @Transactional
//    public void testProcessUserAuthorizationEvent() {
//        var poe = getPoe(AUTH_ON_LOGIN);
//        var walletBeforeEvent = getWallet();
//        poeTransactionService.process(newLoginEvent(USER_LOGIN));
//        var walletAfterEvent = getWallet();
//        Assert.assertEquals((long) poe.getCoinsReward(), walletAfterEvent.getCoinBalance() - walletBeforeEvent.getCoinBalance());
//        Assert.assertEquals((long) poe.getPointsReward(), walletAfterEvent.getExperienceCount() - walletBeforeEvent.getExperienceCount());
//    }

    @Test
    @Transactional
    public void testProcessNftAddedEvent() {
        var poe = getPoe(EventType.CHALLENGE_COMPLETED);
        var walletBeforeEvent = getWallet();
        var userId = walletBeforeEvent.getUserProfile().getKeycloakUserId();
        var celebrityId = walletBeforeEvent.getCelebrity().getId();
        var event = NftAddedEvent.builder()
                .eventType(EventType.CHALLENGE_COMPLETED)
                .nftId(UUID.randomUUID())
                .celebrityId(celebrityId)
                .userId(userId)
                .build();
        poeTransactionService.createPoeTransaction(poeTransactionMapper.toRequestDto(event));
        var walletAfterEvent = getWallet();
        Assert.assertEquals((long) poe.getCoinsReward(), walletAfterEvent.getCoinBalance() - walletBeforeEvent.getCoinBalance());
        Assert.assertEquals((long) poe.getPointsReward(), walletAfterEvent.getExperienceCount() - walletBeforeEvent.getExperienceCount());
    }

    private Poe getPoe(EventType type) {
        return poeRepository.findByCode(EnumUtil.EVENT_TO_POE_MAP.get(type)).orElseThrow();
    }

    private ProfileWallet getWallet() {
        return userProfileService.findByLogin(USER_LOGIN).orElseThrow().getProfileWallets().stream()
                .filter(w -> w.getCelebrity().getId().equals(USER_CELEB))
                .findFirst()
                .orElseThrow();
    }

    private static AuthUserAuthorizedEvent newLoginEvent(String login) {
        var event = new AuthUserAuthorizedEvent();
        event.setLogin(login);
        event.setEventType(AUTH_ON_LOGIN);
        return event;
    }
}