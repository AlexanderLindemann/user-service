package com.nft.platform.service.poe;

import com.nft.platform.common.dto.RewardResponseDto;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.common.event.AuthUserAuthorizedEvent;
import com.nft.platform.common.util.EnumUtil;
import com.nft.platform.domain.Period;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.domain.poe.PoeTransaction_;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.dto.poe.request.LeaderboardRequestDto;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.request.UserBalanceRequestDto;
import com.nft.platform.dto.poe.response.LeaderboardFullResponseDto;
import com.nft.platform.dto.poe.response.LeaderboardResponseDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.dto.poe.response.PoeTransactionUserHistoryDto;
import com.nft.platform.dto.poe.response.UserActivityBalancePositionResponseDto;
import com.nft.platform.dto.poe.response.UserIdActivityBalancePositionResponseDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.mapper.UserProfileMapper;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.mapper.poe.PoeTransactionToUserHistoryMapper;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.repository.poe.PoeTransactionRepository;
import com.nft.platform.repository.poe.UserBalance;
import com.nft.platform.repository.spec.PoeTransactionSpecifications;
import com.nft.platform.service.NotificationSenderService;
import com.nft.platform.service.ProfileWalletService;
import com.nft.platform.service.UserPointsService;
import com.nft.platform.service.billing.BillingService;
import com.nft.platform.util.CommonUtils;
import com.nft.platform.util.security.SecurityUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class PoeTransactionService {

    private final PeriodRepository periodRepository;
    private final UserProfileRepository userProfileRepository;
    private final PoeTransactionRepository poeTransactionRepository;
    private final UserPointsService userPointsService;
    private final ProfileWalletService profileWalletService;
    private final PoeRepository poeRepository;
    private final PoeTransactionMapper poeTransactionMapper;
    private final UserProfileMapper userProfileMapper;
    private final PoeTransactionToUserHistoryMapper poeTransactionToUserHistoryMapper;
    private final SecurityUtil securityUtil;
    private final BillingService billingService;
    private final PoeTransactionFactory poeTransactionFactory;
    private final NotificationSenderService notificationSenderService;


    @Transactional(readOnly = true)
    public List<PoeTransactionUserHistoryDto> findLastPoeHistory(UUID celebrityId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(PoeTransaction_.CREATED_AT).descending());
        Specification<PoeTransaction> spec = PoeTransactionSpecifications.forPoeUserHistory(celebrityId);
        return poeTransactionRepository.findAll(spec, pageable).stream()
                .map(poeTransactionToUserHistoryMapper::convert)
                .collect(Collectors.toList());
    }

    @Transactional
    @Retryable(value = DataIntegrityViolationException.class, backoff = @Backoff(delay = 100, maxDelay = 300))
    public PoeTransactionResponseDto process(PoeTransactionRequestDto request) {
        log.info("Try to create PoeTransaction from dto={}", request);
        var transaction = poeTransactionFactory.create(request);
        execute(transaction, request.getAmount(), request.getEventType());
        return poeTransactionMapper.toDto(transaction);
    }

    @Transactional
    public void process(AuthUserAuthorizedEvent event) {
        log.info("Trying create and execute poe transactions for authorization event {}", event);
        poeTransactionFactory.create(event).forEach(t -> execute(t, 1, event.getEventType()));
    }

    private void execute(PoeTransaction transaction, int amount, EventType eventType) {
        billingService.award(transaction, amount);
        poeTransactionRepository.save(transaction);
        userPointsService.createOrUpdateUserPoints(
                transaction.getPeriodId(),
                transaction.getUserId(),
                transaction.getPointsReward()
        );
        notificationSenderService.sendRewardToNotificationService(eventType, transaction);

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
    public Long calculateUserAllActivityBalance(UserBalanceRequestDto requestDto) {
        log.info("Try to calculate UserAllActivityBalance from dto={}", requestDto);
        Period period = calculatePeriodIfNull(requestDto.getPeriodId());
        Long allActivityBalance = poeTransactionRepository.calculateUserAllActivityBalance(
                requestDto.getUserId(),
                period.getId()
        );
        return allActivityBalance == null ? 0 : allActivityBalance;
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

    public LeaderboardFullResponseDto calculateUsersActivityBalance(UUID periodId) {
        log.info("Try to calculate LeaderboardFull periodId={}", periodId);
        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new ItemNotFoundException(Period.class, periodId));
        List<UserBalance> userBalances = poeTransactionRepository
                .calculateUsersActivityBalance(period.getId());
        List<UserIdActivityBalancePositionResponseDto> leaderboard = userBalances.stream()
                .map(this::userBalanceToDto)
                .collect(Collectors.toList());
        return LeaderboardFullResponseDto.builder()
                .leaderboard(leaderboard)
                .build();
    }

    private UserIdActivityBalancePositionResponseDto userBalanceToDto(UserBalance userBalance) {
        return UserIdActivityBalancePositionResponseDto.builder()
                .activityBalance(userBalance.getActivityBalance())
                .position(userBalance.getRowNumber())
                .keycloakUserId(userBalance.getUserId())
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

    @Transactional(readOnly = true)
    public List<RewardResponseDto> getActionReward(List<UUID> actionIds, UUID clientId, Set<UUID> celebrityId) {
        List<PoeAction> poeActionList = getPoeActions();
        List<RewardResponseDto> rewardList = new ArrayList<>();
        List<Poe> actionPoe = poeRepository.findAll().stream()
                .filter(poe -> poeActionList.contains(poe.getCode()))
                .collect(Collectors.toList());
        List<PoeTransaction> byActionIdInPoeInAndUserId = poeTransactionRepository.
                findByActionIdInAndPoeInAndUserId(actionIds, actionPoe, clientId);
        Map<UUID, List<PoeTransaction>> PoeTransactionMap = new HashMap<>();
        actionIds.forEach(id -> PoeTransactionMap.put(id, getListActionRewards(id, byActionIdInPoeInAndUserId)));
        for (UUID action : actionIds) {
            setReceivedRewards(rewardList, PoeTransactionMap, action);
            setUnclaimedRewards(clientId, rewardList, action, celebrityId);
        }
        return rewardList;
    }

    /**
     * Checks if user already has a once-per-period poe transaction in specific period.
     *
     * @param periodId  - period UUID
     * @param userId    - user UUID
     * @param poeAction - poe action
     * @return - whether if poe action is invalid for specific period
     */
    public boolean isPoeInvalidForPeriod(@NonNull UUID periodId, @NonNull UUID userId, @NonNull PoeAction poeAction) {
        if (EnumUtil.ONCE_FOR_ACTIVE_PERIOD_POE.contains(poeAction)) {
            return poeTransactionRepository.existsByPeriodIdAndUserIdAndPoe_Code(periodId, userId, poeAction);
        }
        return false;
    }

    private void setUnclaimedRewards(UUID clientId, List<RewardResponseDto> rewardList, UUID actionId, Set<UUID> celebrityIds) {
        List<PoeAction> poeActionList = getPoeActions();
        Map<UUID, Boolean> userSubscriberMao = new HashMap<>();
        celebrityIds.forEach(celebrityId ->
                userSubscriberMao.put(celebrityId, profileWalletService.isUserSubscriber(clientId, celebrityId)));
        Map<PoeAction, Poe> actionPoeMap = poeRepository.findAll().stream().collect(Collectors.toMap(Poe::getCode, poe -> poe));
        celebrityIds.forEach(celebrityId -> {
            boolean userSubscriber = userSubscriberMao.get(celebrityId);
            for (PoeAction poeAction : poeActionList) {
                if (rewardList.stream()
                        .noneMatch(reward -> reward.getPoeAction().equals(poeAction) && reward.getActionId().equals(actionId))) {
                    if (userSubscriber) {
                        Poe poe = actionPoeMap.get(poeAction);
                        if (Objects.nonNull(poe)) {
                            rewardList.add(RewardResponseDto.builder()
                                    .actionId(actionId)
                                    .isReceived(false)
                                    .poeAction(poeAction)
                                    .coins(CommonUtils.toPrimitive(poe.getCoinsReward()) + CommonUtils.toPrimitive(poe.getCoinsRewardSub()))
                                    .poe(CommonUtils.toPrimitive(poe.getPointsReward()) + CommonUtils.toPrimitive(poe.getPointsRewardSub()))
                                    .build());
                        }
                    } else {
                        Poe poe = actionPoeMap.get(poeAction);
                        if (Objects.nonNull(poe)) {
                            rewardList.add(RewardResponseDto.builder()
                                    .actionId(actionId)
                                    .isReceived(false)
                                    .poeAction(poeAction)
                                    .coins(CommonUtils.toPrimitive(poe.getCoinsReward()))
                                    .poe(CommonUtils.toPrimitive(poe.getPointsReward()))
                                    .build());
                        }
                    }
                }
            }
        });

    }

    private List<PoeAction> getPoeActions() {
        return Arrays.asList(PoeAction.LIKE,
                PoeAction.SHARE,
                PoeAction.QUIZ,
                PoeAction.CHALLENGE,
                PoeAction.VOTE,
                PoeAction.WHEEL_ROLLED);
    }

    private void setReceivedRewards(List<RewardResponseDto> rewardList, Map<UUID, List<PoeTransaction>> PoeTransactionMap, UUID feedId) {
        List<PoeTransaction> poeTransactions = PoeTransactionMap.get(feedId);
        if (poeTransactions != null) {
            for (PoeTransaction poeTransaction : poeTransactions) {
                rewardList.add(RewardResponseDto.builder()
                        .id(poeTransaction.getId())
                        .actionId(feedId)
                        .isReceived(true)
                        .poeAction(poeTransaction.getPoe().getCode())
                        .coins(CommonUtils.toPrimitive(poeTransaction.getCoinsReward()))
                        .poe(CommonUtils.toPrimitive(poeTransaction.getPointsReward()))
                        .build());
            }
        }
    }

    private List<PoeTransaction> getListActionRewards(UUID id, List<PoeTransaction> listPoe) {
        List<PoeTransaction> rewardList = new ArrayList<>();
        Map<Poe, PoeTransaction> poeTransactionMap = listPoe.stream()
                .filter(poeTransaction -> poeTransaction.getActionId().equals(id) && poeTransaction.getPoe().getCode() != PoeAction.VOTE)
                .collect(Collectors.toMap(PoeTransaction::getPoe, poeTransaction -> poeTransaction, (existing, replacement) -> existing));
        Map<UUID, PoeTransaction> voteRewardMap = getCountPointCoinVoteReward(id, listPoe);
        rewardList.addAll(poeTransactionMap.values());
        rewardList.addAll(voteRewardMap.values());
        return rewardList;
    }

    private Map<UUID, PoeTransaction> getCountPointCoinVoteReward(UUID id, List<PoeTransaction> listPoe) {
        List<PoeTransaction> voteRewards = listPoe.stream()
                .filter(poe -> poe.getPoe().getCode() == PoeAction.VOTE && poe.getActionId().equals(id))
                .collect(Collectors.toList());
        Map<UUID, PoeTransaction> voteRewardMap = new HashMap<>();
        if (!voteRewards.isEmpty()) {
            if (voteRewards.size() > 1) {
                for (PoeTransaction poeTransaction : voteRewards) {
                    if (voteRewardMap.containsKey(poeTransaction.getActionId())) {
                        PoeTransaction poeTransactionFromMap = voteRewardMap.get(poeTransaction.getActionId());
                        voteRewardMap.get(poeTransaction.getActionId())
                                .setCoinsReward(poeTransactionFromMap.getCoinsReward() + poeTransaction.getCoinsReward());
                        voteRewardMap.get(poeTransaction.getActionId())
                                .setPointsReward(poeTransactionFromMap.getPointsReward() + poeTransaction.getPointsReward());
                    } else {
                        voteRewardMap.put(poeTransaction.getActionId(), poeTransaction);
                    }
                }
            } else voteRewardMap.put(voteRewards.get(0).getActionId(), voteRewards.get(0));
        }
        return voteRewardMap;
    }
}
