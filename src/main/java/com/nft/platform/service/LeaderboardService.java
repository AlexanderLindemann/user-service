package com.nft.platform.service;

import com.nft.platform.domain.LeaderboardRow;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.enums.LeaderboardGroup;
import com.nft.platform.dto.response.LeaderboardPositionDto;
import com.nft.platform.dto.response.LeaderboardUserDto;
import com.nft.platform.dto.response.LeaderboardV2ResponseDto;
import com.nft.platform.exception.RestException;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.util.LeaderboardQueryUtils;
import com.nft.platform.util.security.KeycloakUserProfile;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardService {

    private final EntityManager entityManager;
    private final UserProfileRepository userProfileRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public void refreshView() {
        entityManager.createNativeQuery(LeaderboardQueryUtils.REFRESH_QUERY)
                .executeUpdate();
    }

    @Transactional(readOnly = true)
    public LeaderboardGroup calculateCurrentUserCohort() {
        UUID userId = securityUtil.getCurrentUserId();
        Optional<LeaderboardGroup> leaderboardRow = calculateLeaderboardRowsWithCohort(userId).stream()
                .filter(r -> r.getKeycloakUserId().equals(userId))
                .findFirst()
                .map(LeaderboardRow::getUserGroup);
        return leaderboardRow.orElse(LeaderboardGroup.NOT_TOP_50);
    }

    @Transactional(readOnly = true)
    public LeaderboardV2ResponseDto calculateLeaderboard() {
        KeycloakUserProfile currentUser = securityUtil.getCurrentUserOrNull();
        if (currentUser == null) {
            return calculateLeaderboardForUnauthorized();
        }
        return calculateLeaderboardAuthorized();
    }

    private LeaderboardV2ResponseDto calculateLeaderboardForUnauthorized() {
        List<LeaderboardRow> leaderboardRows = findLeaderboardTop6AndLastRow();
        if (leaderboardRows.size() == 0) {
            log.info("Leaderboard for unauthorized is empty");
            return new LeaderboardV2ResponseDto();
        }
        Comparator<LeaderboardRow> comparator = Comparator.comparing(LeaderboardRow::getRowNumber);
        List<LeaderboardRow> rowsWithTop10CohortsSorted = calculateAndSetCohortAndFindTop10Cohort(leaderboardRows).stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        List<LeaderboardPositionDto> firstBlock = rowsWithTop10CohortsSorted.stream()
                .map(r -> mapLeaderboardRowToDto(r, null))
                .collect(Collectors.toList());
        LeaderboardV2ResponseDto leaderboardV2ResponseDto = LeaderboardV2ResponseDto.builder()
                .firstBlock(firstBlock)
                .build();
        findAndSetLeaderboardUserInfo(leaderboardV2ResponseDto);
        return leaderboardV2ResponseDto;
    }

    @SuppressWarnings("unchecked")
    private List<LeaderboardRow> findLeaderboardTop6AndLastRow() {
        List<Object[]> results = entityManager.createNativeQuery(LeaderboardQueryUtils.FIND_LEADERBOARD_TOP_6_AND_LAST)
                .getResultList();
        return results.stream()
                .map(this::mapResultOfLeaderboardQueryToLeaderboardRow)
                .collect(Collectors.toList());
    }

    private Set<LeaderboardRow> calculateAndSetCohortAndFindTop10Cohort(List<LeaderboardRow> leaderboardRows) {
        LeaderboardRow lastRow = calculateLastRow(leaderboardRows);
        List<LeaderboardRow> top6Rows = leaderboardRows.stream()
                .filter(r -> r.getUserGroup() == null)
                .collect(Collectors.toList());
        Set<LeaderboardRow> rowsWithCohorts;
        if (lastRow.getRowNumber() < 11) {
            log.info("Leaderboard for unauthorized size less than 11");
            rowsWithCohorts = calculateAndSetCohortsWhenLess11(top6Rows);
        } else {
            rowsWithCohorts = top6Rows.stream()
                    .peek(row -> {
                        if (row.getRowNumber() * 100 / 10 / lastRow.getRowNumber() <= 10) {
                            row.setUserGroup(LeaderboardGroup.TOP_10);
                        }
                    })
                    .collect(Collectors.toSet());
        }
        return rowsWithCohorts.stream()
                .filter(r -> r.getUserGroup() == LeaderboardGroup.TOP_10)
                .collect(Collectors.toSet());
    }

    private LeaderboardV2ResponseDto calculateLeaderboardAuthorized() {
        UUID userId = securityUtil.getCurrentUserId();
        Set<LeaderboardRow> leaderboardRowsWithCohorts = calculateLeaderboardRowsWithCohort(userId);
        if (leaderboardRowsWithCohorts.isEmpty()) {
            log.info("Leaderboard for authorized is empty");
            return new LeaderboardV2ResponseDto();
        }
        LeaderboardV2ResponseDto leaderboardV2ResponseDto = calculateLeaderboardResponseFromRowsWithCohorts(
                leaderboardRowsWithCohorts,
                userId
        );
        findAndSetLeaderboardUserInfo(leaderboardV2ResponseDto);
        return leaderboardV2ResponseDto;
    }

    private Set<LeaderboardRow> calculateLeaderboardRowsWithCohort(UUID userId) {
        List<LeaderboardRow> rowsWithTop11AndCohorts = findLeaderboardRowsWithCohortsCurrentUserAndTop11Users(userId);
        if (rowsWithTop11AndCohorts.size() == 0) {
            log.info("Leaderboard calculateLeaderboardRowsWithCohort is empty");
            return Set.of();
        }
        LeaderboardRow lastRow = calculateLastRow(rowsWithTop11AndCohorts);

        if (lastRow.getRowNumber() < 11) {
            log.info("Leaderboard size less than 11");
            List<LeaderboardRow> leaderboardRows = rowsWithTop11AndCohorts.stream()
                    .filter(r -> r.getUserGroup() == LeaderboardGroup.TOP_10_ROWS)
                    .collect(Collectors.toList());
            return calculateAndSetCohortsWhenLess11(leaderboardRows);
        }
        log.info("Leaderboard size more than 10");
        List<LeaderboardRow> leaderboardRows = rowsWithTop11AndCohorts.stream()
                .filter(r -> r.getUserGroup() != LeaderboardGroup.TOP_10_ROWS)
                .collect(Collectors.toList());
        return calculateAndSetCohortsWhen11AndMore(leaderboardRows, lastRow);

    }

    @SuppressWarnings("unchecked")
    private List<LeaderboardRow> findLeaderboardRowsWithCohortsCurrentUserAndTop11Users(UUID userId) {
        List<Object[]> results = entityManager.createNativeQuery(LeaderboardQueryUtils.CALCULATE_LEADERBOARD_FOR_USER)
                .setParameter("keycloak_user_id", userId)
                .getResultList();
        return results.stream()
                .map(this::mapResultOfLeaderboardQueryToLeaderboardRow)
                .collect(Collectors.toList());
    }

    private LeaderboardRow mapResultOfLeaderboardQueryToLeaderboardRow(Object[] result) {
        int rowNumber = (int) result[0];
        UUID keycloakUserId = UUID.fromString((String) result[1]);
        int points = (int) result[2];
        String userGroup = (String) result[3];
        LeaderboardRow leaderboardRow = LeaderboardRow.builder()
                .rowNumber(rowNumber)
                .keycloakUserId(keycloakUserId)
                .points(points)
                .build();
        if (userGroup != null) {
            leaderboardRow.setUserGroup(LeaderboardGroup.valueOf(userGroup));
        }
        return leaderboardRow;
    }

    private LeaderboardRow calculateLastRow(List<LeaderboardRow> rows) {
        LeaderboardRow lastRow = null;
        for (LeaderboardRow row : rows) {
            if (lastRow == null || row.getRowNumber() > lastRow.getRowNumber()) {
                lastRow = row;
            }
        }
        return lastRow;
    }

    private LeaderboardV2ResponseDto calculateLeaderboardResponseFromRowsWithCohorts(
            Set<LeaderboardRow> leaderboardRows,
            UUID userId
    ) {
        List<LeaderboardRow> leaderboardRowsSorted = leaderboardRows.stream()
                .sorted(Comparator.comparing(LeaderboardRow::getRowNumber))
                .collect(Collectors.toList());
        LeaderboardRow currentUserRow = leaderboardRowsSorted.stream()
                .filter(r -> r.getKeycloakUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (currentUserRow == null) {
            return calculateLeaderboardIfCurrentUserNull(leaderboardRowsSorted);
        }

        if (currentUserRow.getUserGroup() == LeaderboardGroup.TOP_10) {
            return calculateLeaderboardIfCurrentUserInTop10(currentUserRow, leaderboardRowsSorted);
        }

        return calculateLeaderboardIfCurrentUserExistsAndNotTop10(currentUserRow, leaderboardRowsSorted);
    }

    private Set<LeaderboardRow> calculateAndSetCohortsWhenLess11(List<LeaderboardRow> leaderboardRows) {
        int leaderboardRowsSize = leaderboardRows.size();
        Map<LeaderboardGroup, Integer> userAmountByCohort = new HashMap<>();
        userAmountByCohort.put(LeaderboardGroup.TOP_10, 0);
        userAmountByCohort.put(LeaderboardGroup.TOP_20, 0);
        userAmountByCohort.put(LeaderboardGroup.TOP_30, 0);
        userAmountByCohort.put(LeaderboardGroup.TOP_40, 0);
        userAmountByCohort.put(LeaderboardGroup.TOP_50, 0);
        int sizeDiv5 = leaderboardRowsSize / 5;
        int sizeMod5 = leaderboardRowsSize % 5;

        for (int i = 1; i <= userAmountByCohort.size(); i++) {
            LeaderboardGroup leaderboardGroup = LeaderboardGroup.findByNumber(i);
            userAmountByCohort.put(leaderboardGroup, userAmountByCohort.get(leaderboardGroup) + sizeDiv5);
        }
        for (int i = 1; i <= sizeMod5; i++) {
            LeaderboardGroup leaderboardGroup = LeaderboardGroup.findByNumber(i);
            userAmountByCohort.put(leaderboardGroup, userAmountByCohort.get(leaderboardGroup) + 1);
        }

        int startRow = 0;
        Map<Integer, LeaderboardGroup> cohortByRowNumber = new HashMap<>();
        for (int i = 1; i <= userAmountByCohort.size(); i++) {
            LeaderboardGroup cohort = LeaderboardGroup.findByNumber(i);
            int userAmount = userAmountByCohort.get(cohort);
            for (int j = 0; j < userAmount; j++) {
                startRow += 1;
                cohortByRowNumber.put(startRow, cohort);
            }
        }

        for (LeaderboardRow row : leaderboardRows) {
            row.setUserGroup(cohortByRowNumber.get(row.getRowNumber()));
        }

        return new HashSet<>(leaderboardRows);
    }

    private Set<LeaderboardRow> calculateAndSetCohortsWhen11AndMore(
            List<LeaderboardRow> leaderboardRows,
            LeaderboardRow lastRow
    ) {
        int lastRowNumber = lastRow.getRowNumber();
        return leaderboardRows.stream()
                .peek(r -> {
                    if (r.getUserGroup() == null) {
                        int percent = r.getRowNumber() * 100 / lastRowNumber;
                        LeaderboardGroup leaderboardGroup = LeaderboardGroup.fromPercent(percent);
                        r.setUserGroup(leaderboardGroup);
                    }
                })
                .collect(Collectors.toSet());
    }

    private LeaderboardV2ResponseDto calculateLeaderboardIfCurrentUserNull(List<LeaderboardRow> leaderboardRowsSorted) {
        LeaderboardV2ResponseDto leaderboardV2ResponseDto = new LeaderboardV2ResponseDto();
        List<LeaderboardPositionDto> firstBlock = new ArrayList<>();
        for (LeaderboardRow row : leaderboardRowsSorted) {
            if (row.getUserGroup() == LeaderboardGroup.TOP_10 && firstBlock.size() < 3) {
                LeaderboardPositionDto position = mapLeaderboardRowToDto(row, null);
                firstBlock.add(position);
            }
        }
        leaderboardV2ResponseDto.setFirstBlock(firstBlock);
        return leaderboardV2ResponseDto;
    }

    private LeaderboardV2ResponseDto calculateLeaderboardIfCurrentUserInTop10(
            LeaderboardRow currentUserRow,
            List<LeaderboardRow> leaderboardRowsSorted
    ) {
        LeaderboardV2ResponseDto leaderboardV2ResponseDto = new LeaderboardV2ResponseDto();
        List<LeaderboardPositionDto> firstBlock = new ArrayList<>();
        List<LeaderboardPositionDto> thirdBlock = new ArrayList<>();
        for (LeaderboardRow row : leaderboardRowsSorted) {
            if (row.getUserGroup() == LeaderboardGroup.TOP_10 && firstBlock.size() < 3) {
                LeaderboardPositionDto position = mapLeaderboardRowToDto(row, currentUserRow);
                firstBlock.add(position);
            }
            if (shouldAddRowToThirdBlockThenCurrentUserTop10(row, currentUserRow)) {
                LeaderboardPositionDto position = mapLeaderboardRowToDto(row, currentUserRow);
                if (!firstBlock.contains(position)) {
                    thirdBlock.add(position);
                }
            }
        }
        leaderboardV2ResponseDto.setFirstBlock(firstBlock);
        leaderboardV2ResponseDto.setThirdBlock(thirdBlock);

        return leaderboardV2ResponseDto;
    }

    private boolean shouldAddRowToThirdBlockThenCurrentUserTop10(LeaderboardRow row, LeaderboardRow currentUserRow) {
        return currentUserRow.getRowNumber() > 3
                && (row.getRowNumber() == currentUserRow.getRowNumber()
                || row.getRowNumber() == currentUserRow.getRowNumber() - 1
                || row.getRowNumber() == currentUserRow.getRowNumber() + 1);
    }

    private LeaderboardV2ResponseDto calculateLeaderboardIfCurrentUserExistsAndNotTop10(
            LeaderboardRow currentUserRow,
            List<LeaderboardRow> leaderboardRowsSorted
    ) {
        LeaderboardV2ResponseDto leaderboardV2ResponseDto = LeaderboardV2ResponseDto.builder()
                .build();
        List<LeaderboardPositionDto> firstBlock = new ArrayList<>();
        for (LeaderboardRow row : leaderboardRowsSorted) {
            if (row.getUserGroup() == LeaderboardGroup.TOP_10 && row.getRowNumber() < 4 && firstBlock.size() < 3) {
                LeaderboardPositionDto position = mapLeaderboardRowToDto(row, currentUserRow);
                firstBlock.add(position);
            }
        }
        leaderboardV2ResponseDto.setFirstBlock(firstBlock);

        setSecondBlockAsLastFromPreviousCohort(
                leaderboardV2ResponseDto,
                currentUserRow,
                leaderboardRowsSorted,
                firstBlock
        );
        setThirdBlockAsUserWithNeighbors(
                leaderboardV2ResponseDto,
                currentUserRow,
                leaderboardRowsSorted
        );
        return leaderboardV2ResponseDto;
    }

    private void setSecondBlockAsLastFromPreviousCohort(
            LeaderboardV2ResponseDto leaderboardV2ResponseDto,
            LeaderboardRow currentUser,
            List<LeaderboardRow> leaderboardRows,
            List<LeaderboardPositionDto> firstBlock
    ) {
        LeaderboardGroup previous = LeaderboardGroup.findPrevious(currentUser.getUserGroup());
        LeaderboardRow lastInPrevious = null;
        for (LeaderboardRow row : leaderboardRows) {
            if (row.getUserGroup() == previous && (lastInPrevious == null || row.getRowNumber() > lastInPrevious.getRowNumber())) {
                lastInPrevious = row;
            }
        }
        if (lastInPrevious == null) {
            throw new RestException("Leaderboard not found last user in previous cohort", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<LeaderboardPositionDto> secondBlock = new ArrayList<>();
        LeaderboardPositionDto position = mapLeaderboardRowToDto(lastInPrevious, currentUser);
        if (!firstBlock.contains(position)) {
            secondBlock.add(position);
        }
        leaderboardV2ResponseDto.setSecondBlock(secondBlock);
    }

    private void setThirdBlockAsUserWithNeighbors(
            LeaderboardV2ResponseDto leaderboardV2ResponseDto,
            LeaderboardRow currentUser,
            List<LeaderboardRow> leaderboardRows
    ) {
        LeaderboardRow currentUserOneMore = null;
        LeaderboardRow currentUserOneLess = null;
        for (LeaderboardRow row : leaderboardRows) {
            if (row.getRowNumber() == currentUser.getRowNumber() + 1) {
                currentUserOneMore = row;
            }
            if (row.getRowNumber() == currentUser.getRowNumber() - 1) {
                currentUserOneLess = row;
            }
        }
        if (currentUserOneLess == null) {
            log.error("Leaderboard previous user is null. UserId={}, rowNumber={}",
                    currentUser.getKeycloakUserId(), currentUser.getRowNumber());
            throw new RestException("Leaderboard previous user is null", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<LeaderboardPositionDto> thirdBlock = new ArrayList<>();
        if (currentUserOneLess.getUserGroup() == currentUser.getUserGroup()) {
            LeaderboardPositionDto position = mapLeaderboardRowToDto(currentUserOneLess, currentUser);
            thirdBlock.add(position);
        }
        LeaderboardPositionDto position2 = mapLeaderboardRowToDto(currentUser, currentUser);
        thirdBlock.add(position2);
        if (currentUserOneMore != null) {
            LeaderboardPositionDto position3 = mapLeaderboardRowToDto(currentUserOneMore, currentUser);
            thirdBlock.add(position3);
        }
        leaderboardV2ResponseDto.setThirdBlock(thirdBlock);
    }

    private LeaderboardPositionDto mapLeaderboardRowToDto(LeaderboardRow row, LeaderboardRow currentUser) {
        return LeaderboardPositionDto.builder()
                .position(row.getRowNumber())
                .cohort(row.getUserGroup())
                .pointsBalance(row.getPoints())
                .currentUser(currentUser != null && currentUser.getKeycloakUserId().equals(row.getKeycloakUserId()))
                .userDto(
                        LeaderboardUserDto.builder()
                                .keycloakUserId(row.getKeycloakUserId())
                                .build()
                )
                .build();
    }

    private void findAndSetLeaderboardUserInfo(LeaderboardV2ResponseDto leaderboardV2ResponseDto) {
        List<UUID> firstBlockUserIds = leaderboardV2ResponseDto.getFirstBlock().stream()
                .map(r -> r.getUserDto().getKeycloakUserId())
                .collect(Collectors.toList());
        List<UUID> secondBlockUserIds = leaderboardV2ResponseDto.getSecondBlock().stream()
                .map(r -> r.getUserDto().getKeycloakUserId())
                .collect(Collectors.toList());
        List<UUID> thirdBlockUserIds = leaderboardV2ResponseDto.getThirdBlock().stream()
                .map(r -> r.getUserDto().getKeycloakUserId())
                .collect(Collectors.toList());

        Set<UUID> allUserIds = new HashSet<>();
        allUserIds.addAll(firstBlockUserIds);
        allUserIds.addAll(secondBlockUserIds);
        allUserIds.addAll(thirdBlockUserIds);

        List<UserProfile> userProfiles = userProfileRepository.findByKeycloakUserIdIn(allUserIds);

        mapUserProfilesToPositionDtos(userProfiles, leaderboardV2ResponseDto.getFirstBlock());
        mapUserProfilesToPositionDtos(userProfiles, leaderboardV2ResponseDto.getSecondBlock());
        mapUserProfilesToPositionDtos(userProfiles, leaderboardV2ResponseDto.getThirdBlock());
    }

    private void mapUserProfilesToPositionDtos(List<UserProfile> userProfiles, List<LeaderboardPositionDto> positions) {
        positions.forEach(position -> {
                    UUID keycloakUserId = position.getUserDto().getKeycloakUserId();
                    UserProfile userProfile = userProfiles.stream().filter(u -> u.getKeycloakUserId().equals(keycloakUserId))
                            .findFirst()
                            .orElseThrow(() -> new RestException(
                                    "Leaderboard member with keycloakUserId=" + keycloakUserId + " not found",
                                    HttpStatus.INTERNAL_SERVER_ERROR)
                            );
                    position.getUserDto().setUsername(calculateDisplayedUserNameInLeaderboard(userProfile));
                    position.getUserDto().setImageUrl(userProfile.getImageUrl());
                }
        );
    }

    private String calculateDisplayedUserNameInLeaderboard(UserProfile userProfile) {
        String displayedName;
        if (isNeedDisplayNickName(userProfile)) {
            displayedName = userProfile.getNickname();
        } else if (userProfile.getFirstName() != null && userProfile.getLastName() != null) {
            displayedName = userProfile.getFirstName() + " " + userProfile.getLastName();
        } else if (userProfile.getFirstName() != null) {
            displayedName = userProfile.getFirstName();
        } else {
            displayedName = userProfile.getLastName();
        }

        return displayedName != null ? displayedName : "unknown";
    }

    private boolean isNeedDisplayNickName(UserProfile userProfile) {
        return userProfile.isInvisibleName()
                || (userProfile.getFirstName() == null && userProfile.getLastName() == null);
    }
}
