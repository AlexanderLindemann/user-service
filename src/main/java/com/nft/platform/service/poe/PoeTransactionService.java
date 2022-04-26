package com.nft.platform.service.poe;

import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.domain.Period;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.domain.poe.PoeTransaction_;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.dto.poe.request.LeaderboardRequestDto;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.request.UserBalanceRequestDto;
import com.nft.platform.dto.poe.response.LeaderboardResponseDto;
import com.nft.platform.dto.poe.response.PoeTransactionUserHistoryDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.dto.poe.response.UserActivityBalancePositionResponseDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.mapper.UserProfileMapper;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.mapper.poe.PoeTransactionToUserHistoryMapper;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.repository.poe.PoeTransactionRepository;
import com.nft.platform.repository.poe.UserBalance;
import com.nft.platform.repository.spec.PoeTransactionSpecifications;
import com.nft.platform.util.CommonUtils;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PoeTransactionService {

    private final PeriodRepository periodRepository;
    private final UserProfileRepository userProfileRepository;
    private final PoeTransactionRepository poeTransactionRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final PoeRepository poeRepository;
    private final PoeTransactionMapper poeTransactionMapper;
    private final UserProfileMapper userProfileMapper;
    private final PoeTransactionToUserHistoryMapper poeTransactionToUserHistoryMapper;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public List<PoeTransactionUserHistoryDto> findLastPoeHistory(UUID celebrityId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(PoeTransaction_.CREATED_AT).descending());
        Specification<PoeTransaction> spec = PoeTransactionSpecifications.forPoeUserHistory(celebrityId);
        return poeTransactionRepository.findAll(spec, pageable).stream()
                .map(poeTransactionToUserHistoryMapper::convert)
                .collect(Collectors.toList());
    }

    @Transactional
    public PoeTransactionResponseDto createPoeTransaction(PoeTransactionRequestDto requestDto) {
        log.info("Try to create PoeTransaction from dto={}", requestDto);
        PoeAction poeAction = mapEventToPoeAction(requestDto.getEventType());
        if (poeAction == null) {
            throw new ItemNotFoundException(Poe.class, "can not find poe for event=" + requestDto.getEventType());
        }
        Poe poe = poeRepository.findByCode(poeAction)
                .orElseThrow(() -> new ItemNotFoundException(Poe.class, "code=" + poeAction.getActionCode()));
        PoeTransaction poeTransaction = poeTransactionMapper.toEntity(requestDto, new PoeTransaction());
        poeTransaction.setPeriodId(periodRepository.findByStatus(PeriodStatus.ACTIVE).get().getId());
        poeTransaction.setPoe(poe);
        ProfileWallet profileWallet = profileWalletRepository
                .findByKeycloakUserIdAndCelebrityId(requestDto.getUserId(), requestDto.getCelebrityId())
                .orElseThrow(() -> new ItemNotFoundException(ProfileWallet.class,
                        "keycloakUserId=" + requestDto.getUserId() + " celebrityId=" + requestDto.getCelebrityId()));
        int coinsAward = CommonUtils.toPrimitive(poe.getCoinsReward());
        int pointsAward = CommonUtils.toPrimitive(poe.getPointsReward());
        if (profileWallet.isSubscriber()) {
            coinsAward += CommonUtils.toPrimitive(poe.getCoinsRewardSub());
            pointsAward += CommonUtils.toPrimitive(poe.getPointsRewardSub());
        }
        poeTransaction.setPointsReward(pointsAward * requestDto.getAmount());
        poeTransaction.setCoinsReward(coinsAward * requestDto.getAmount());

        if (coinsAward != 0) {
            profileWalletRepository.updateProfileWalletBalance(requestDto.getUserId(), requestDto.getCelebrityId(), coinsAward);
        }
        poeTransactionRepository.save(poeTransaction);
        return poeTransactionMapper.toDto(poeTransaction);
    }

    @Nullable
    private PoeAction mapEventToPoeAction(EventType eventType) {
        PoeAction poeAction;
        switch (eventType) {
            case VOTE_CREATED:
                poeAction = PoeAction.VOTE;
                break;
            case PROFILE_WALLET_CREATED:
                poeAction = PoeAction.REGISTRATION;
                break;
            case LIKE_ADDED:
                poeAction = PoeAction.LIKE;
                break;
            case CHALLENGE_COMPLETED:
                poeAction = PoeAction.CHALLENGE;
                break;
            case QUIZ_COMPLETED:
                poeAction = PoeAction.QUIZ;
                break;
            case FIRST_TIME_PERIOD_APP_OPEN:
                poeAction = PoeAction.PERIOD_ENTRY;
                break;
            default:
                poeAction = null;
        }
        return poeAction;
    }

    @Transactional(readOnly = true)
    public Integer calculateUserActivityBalance(UserBalanceRequestDto requestDto) {
        log.info("Try to calculate UserActivityBalance from dto={}", requestDto);
        Period period = calculatePeriodIfNull(requestDto.getPeriodId());
        Integer activityBalance = poeTransactionRepository.calculateUserActivityBalance(
                requestDto.getUserId(),
                requestDto.getCelebrityId(),
                period.getId()
        );
        return activityBalance == null ? 0 : activityBalance;
    }

    @Transactional(readOnly = true)
    public LeaderboardResponseDto calculateTopUsersActivityBalance(LeaderboardRequestDto requestDto) {
        log.info("Try to calculate UserActivityBalance LeaderBoard from dto={}", requestDto);
        int leaderboardSize = requestDto.getTo() - requestDto.getFrom();
        if (leaderboardSize < 0 || leaderboardSize > 100) {
            throw new RestException("leaderboardSize=" + leaderboardSize + " too large", HttpStatus.BAD_REQUEST);
        }
        Period period = calculatePeriodIfNull(requestDto.getPeriodId());
        UUID userId = securityUtil.getCurrentUserId();
        List<UserBalance> userBalances = poeTransactionRepository
                .calculateTopUsersActivityBalance(period.getId(), userId, requestDto.getFrom(), requestDto.getTo());
        long amountUsers = poeTransactionRepository.countDistinctUserIdByPeriodId(period.getId());

        Map<UUID, UserProfile> userProfileByKeycloakUserId;
        if (!userBalances.isEmpty()) {
            Set<UUID> keycloakUserIds = userBalances.stream()
                    .map(UserBalance::getUserId)
                    .collect(Collectors.toSet());
            userProfileByKeycloakUserId = userProfileRepository.findByKeycloakUserIdIn(keycloakUserIds)
                    .stream()
                    .collect(Collectors.toMap(UserProfile::getKeycloakUserId, userProfile -> userProfile));
        } else {
            userProfileByKeycloakUserId = Map.of();
        }

        UserBalance currentUserBalance = userBalances.stream().filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        if (!isUserInLeaderboard(currentUserBalance, requestDto)) {
            userBalances.remove(currentUserBalance);
        }
        List<UserActivityBalancePositionResponseDto> leaderboard = userBalances.stream()
                .map(userBalance -> userToUserBalancePositionDto(userBalance, userProfileByKeycloakUserId))
                .collect(Collectors.toList());

        UserActivityBalancePositionResponseDto currentUser;
        if (currentUserBalance != null) {
            currentUser = userToUserBalancePositionDto(currentUserBalance, userProfileByKeycloakUserId);
        } else {
            currentUser = null;
        }
        return LeaderboardResponseDto.builder()
                .currentUser(currentUser)
                .leaderboard(leaderboard)
                .amountUsers(amountUsers)
                .build();
    }

    private UserActivityBalancePositionResponseDto userToUserBalancePositionDto(
            UserBalance userBalance,
            Map<UUID, UserProfile> userProfileByKeycloakUserId
    ) {
        UserProfile userProfile = userProfileByKeycloakUserId.get(userBalance.getUserId());
        return UserActivityBalancePositionResponseDto.builder()
                .activityBalance(userBalance.getActivityBalance())
                .position(userBalance.getRowNumber())
                .userLeaderboardResponseDto(userProfileMapper.toUserLeaderboardDto(userProfile))
                .build();
    }

    private boolean isUserInLeaderboard(UserBalance userBalance, LeaderboardRequestDto requestDto) {
        return userBalance != null
                && userBalance.getRowNumber() >= requestDto.getFrom()
                && userBalance.getRowNumber() <= requestDto.getTo();
    }

    private Period calculatePeriodIfNull(UUID periodId) {
        if (periodId != null) {
            return periodRepository.findById(periodId)
                    .orElseThrow(() -> new ItemNotFoundException(Period.class, periodId));
        }
        return periodRepository.findByStatus(PeriodStatus.ACTIVE)
                .orElseThrow(() -> new ItemNotFoundException(Period.class, "currentPeriod"));
    }

}
