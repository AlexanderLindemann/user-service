package com.nft.platform.service.poe;

import com.nft.platform.common.enums.EventType;
import com.nft.platform.domain.Period;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.dto.poe.request.LeaderboardRequestDto;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.request.UserBalanceRequestDto;
import com.nft.platform.dto.poe.response.LeaderboardResponseDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.dto.poe.response.UserActivityBalancePositionResponseDto;
import com.nft.platform.enums.PoeAction;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.mapper.UserProfileMapper;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.repository.poe.PoeTransactionRepository;
import com.nft.platform.repository.poe.UserBalance;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PoeRepository poeRepository;
    private final PoeTransactionMapper poeTransactionMapper;
    private final UserProfileMapper userProfileMapper;
    private final SecurityUtil securityUtil;

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
        poeTransaction.setPeriodId(periodRepository.findFirst1ByOrderByStartTimeDesc().get().getId());
        poeTransaction.setPoeId(poe.getId());
        if (poe.getCoinsReward() != null) {
            poeTransaction.setCoinsReward(poe.getCoinsReward() * requestDto.getAmount());
        }
        if (poe.getPointsReward() != null) {
            poeTransaction.setPointsReward(poe.getPointsReward() * requestDto.getAmount());
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
        return periodRepository.findFirst1ByOrderByStartTimeDesc()
                .orElseThrow(() -> new ItemNotFoundException(Period.class, "currentPeriod"));
    }

}
