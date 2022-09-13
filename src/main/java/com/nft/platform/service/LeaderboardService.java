package com.nft.platform.service;

import com.nft.platform.domain.LeaderboardDto;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.enums.LeaderboardGroup;
import com.nft.platform.dto.response.LeaderboardByIdDto;
import com.nft.platform.dto.response.LeaderboardByIdResponseDto;
import com.nft.platform.dto.response.LeaderboardPositionDto;
import com.nft.platform.dto.response.LeaderboardResponseDto;
import com.nft.platform.dto.response.LeaderboardUserByIdDto;
import com.nft.platform.dto.response.LeaderboardUserDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.repository.LeaderboardRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.util.security.KeycloakUserProfile;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class LeaderboardService {

    public static final int AMOUNT_COHORTS = 5;
    private final LeaderboardRepository leaderboardRepository;
    private final UserProfileRepository userProfileRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public void refreshView() {
        leaderboardRepository.refreshView();
    }

    /**
     * Getting data from the leaderboard for the user and the current user by uuid
     *
     * @param uuid - user UUID
     * @return - a {@link LeaderboardByIdResponseDto} object.
     */
    public LeaderboardByIdResponseDto getLeaderboardByIdResponseDto(UUID uuid) {
        log.debug("getting leaderboard by ResponseDto, userId = {}", uuid);
        return LeaderboardByIdResponseDto.builder()
                .otherUser(getLeaderboardByUserIdAndMapToLeaderboard(uuid))
                .currentUser(getCurrentUserLeaderboardPositionDto())
                .build();
    }


    private LeaderboardByIdDto getLeaderboardByUserIdAndMapToLeaderboard(UUID userId) {
        log.debug("getting leaderboard by userId and map to leaderboard, userId = {}", userId);
        return leaderboardRepository.findLeaderboardByUserId(userId)
                .stream()
                .findFirst()
                .map(this::mapResultOfLeaderboardQueryToLeaderboardByIdDto)
                .orElseGet(() -> getLeaderboardByUserIdIfUserNotFoundLeaderboard(userId));
    }

    private LeaderboardByIdDto getLeaderboardByKeycloakUserIdAndMapToLeaderboard(UUID keycloakUserId) {
        log.debug("getting leaderboard by keycloakUserId and map to leaderboard, keycloakUserId = {}", keycloakUserId);
        return leaderboardRepository.findLeaderboardByKeycloakUserId(keycloakUserId)
                .stream()
                .findFirst()
                .map(this::mapResultOfLeaderboardQueryToLeaderboardByIdDto)
                .orElseGet(() -> getLeaderboardByKeycloakUserIdIfUserNotFoundLeaderboard(keycloakUserId));
    }

    private LeaderboardByIdDto mapResultOfLeaderboardQueryToLeaderboardByIdDto(Object[] result) {
        log.debug("mapping result of leaderboard query to LeaderboardByIdDt");
        return LeaderboardByIdDto.builder()
                .position(Integer.parseInt(String.valueOf(result[0])))
                .pointsBalance(Integer.parseInt(String.valueOf(result[1])))
                .user(LeaderboardUserByIdDto.builder()
                        .username((String) result[3])
                        .userId(UUID.fromString((String) result[2]))
                        .imageUrl((String) result[4])
                        .anonymous((Boolean) result[5])
                        .build())
                .build();
    }


    /**
     * Getting a position with the leaderboard of an authorized user, if he is not there, then it is necessary to display his profile.
     * To do this, we catch the exception that he is not found in the leaderboard and look for his profile in the database
     *
     * @return a {@link LeaderboardByIdDto} object.
     */
    private LeaderboardByIdDto getCurrentUserLeaderboardPositionDto() {
        log.debug("getting current user LeaderboardPositionDto");
        KeycloakUserProfile currentUserOrNull = securityUtil.getCurrentUserOrNull();
        if (Objects.nonNull(currentUserOrNull)) {
            return getLeaderboardByKeycloakUserIdAndMapToLeaderboard(UUID.fromString(currentUserOrNull.getId()));
        }
        return null;
    }


    private LeaderboardByIdDto getLeaderboardByKeycloakUserIdIfUserNotFoundLeaderboard(UUID keycloakUserId) {
        log.debug("getting leaderboard by keycloakUserId if user not found Leaderboard, keycloakUserId = {}", keycloakUserId);
        return userProfileRepository.findLeaderboardUserByIdDtoByKeycloakUserId(keycloakUserId)
                .map(buildLeaderBoardFunction())
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
    }

    private LeaderboardByIdDto getLeaderboardByUserIdIfUserNotFoundLeaderboard(UUID userId) {
        log.debug("getting LeaderboardByUserId if user not found Leaderboard, userId = {}", userId);
        return userProfileRepository.findLeaderboardUserByUserId(userId)
                .map(buildLeaderBoardFunction())
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, userId));
    }

    private Function<LeaderboardUserByIdDto, LeaderboardByIdDto> buildLeaderBoardFunction() {
        return userProfile ->
                LeaderboardByIdDto.builder()
                        .position(null)
                        .pointsBalance(0)
                        .user(userProfile)
                        .build();
    }


    /**
     * Getting all users from the leaderboard , distributing them into cohorts and calculating their place in each cohort
     * @return a {@link LeaderboardResponseDto} object.
     */
    public LeaderboardResponseDto getLeaderboardResponseDto() {
        List<LeaderboardDto> allLeaderboardListAndSetCohort = getAllLeaderboardListAndSetCohort();
        KeycloakUserProfile currentUserProfile = securityUtil.getCurrentUserOrNull();
        if (Objects.isNull(currentUserProfile)) {
            return calculateLeaderboardForUnauthorized(allLeaderboardListAndSetCohort);
        }
        return allLeaderboardListAndSetCohort.stream()
                .filter(user -> UUID.fromString(currentUserProfile.getId()).equals(user.getKeycloakUserId()))
                .findFirst()
                .map(currentUser -> calculateLeaderboardIfCurrentUserNotNull(allLeaderboardListAndSetCohort, currentUser))
                .orElseGet(() -> calculateLeaderboardForUnauthorized(allLeaderboardListAndSetCohort));
    }

    private List<LeaderboardDto> getAllLeaderboardListAndSetCohort() {
        List<LeaderboardDto> allLeaderboardList = getAllLeaderboard();
        return calculateAndSetCohort(allLeaderboardList);
    }

    public List<LeaderboardDto> calculateAndSetCohort(List<LeaderboardDto> allLeaderboardList) {
        if (allLeaderboardList.size() < 12) {
            return calculateAndSetCohortsWhenLess11(allLeaderboardList);
        } else {
            return calculateAndSetCohortsWhenMore11(allLeaderboardList);
        }
    }

    /**
     * if unauthorized, then return the first 6 records of the top 10% in the first block
     *
     * @param allLeaderboardList - all Leaderboard from the database
     * @return a {@link List<LeaderboardResponseDto>}
     */
    private LeaderboardResponseDto calculateLeaderboardForUnauthorized(List<LeaderboardDto> allLeaderboardList) {
        log.debug("calculate leaderboard is user unauthorized, Leaderboard size = {}", allLeaderboardList.size());
        return LeaderboardResponseDto.builder()
                .firstBlock(allLeaderboardList.stream()
                        .filter(leaderboard -> LeaderboardGroup.TOP_10 == leaderboard.getUserGroup())
                        .map(leaderboard -> mapLeaderboardRowToDto(leaderboard, null))
                        .limit(6)
                        .collect(Collectors.toList()))
                .build();
    }

    private LeaderboardResponseDto calculateLeaderboardIfCurrentUserNotNull(List<LeaderboardDto> allLeaderboardList, LeaderboardDto currentUser) {
        log.debug("calculate leaderboard is user not null, Leaderboard size = {} , currentUser = {}", allLeaderboardList.size(), currentUser.toString());
        LeaderboardResponseDto leaderboardV2ResponseDtoNew = null;
        switch (currentUser.getUserGroup()) {
            case TOP_10:
                leaderboardV2ResponseDtoNew = calculateLeaderboardIfUserInTop10(allLeaderboardList, currentUser);
                break;
            case TOP_20:
                leaderboardV2ResponseDtoNew = calculateLeaderboardIfUserInTop20(allLeaderboardList, currentUser);
                break;
            case TOP_30:
            case TOP_40:
            case TOP_50:
            case NOT_TOP_50:
                leaderboardV2ResponseDtoNew = calculateLeaderboardIfUserInTop304050NonTop(allLeaderboardList, currentUser);
                break;
        }
        return leaderboardV2ResponseDtoNew;
    }

    private LeaderboardResponseDto calculateLeaderboardIfUserInTop304050NonTop(List<LeaderboardDto> allLeaderboardList,
                                                                               LeaderboardDto currentUser) {
        log.debug("calculate leaderboard if user in top 304050NonTop, Leaderboard size = {} , currentUser = {}", allLeaderboardList.size(), currentUser.toString());
        TreeMap<Integer, LeaderboardDto> leaderboardFirstBlockMap = new TreeMap<>();
        TreeMap<Integer, LeaderboardDto> leaderboardSecondBlockMap = new TreeMap<>();
        TreeMap<Integer, LeaderboardDto> leaderboardThirdBlockMap = new TreeMap<>();
        LeaderboardGroup secondBlocLeaderboard = LeaderboardGroup.findByNumber(currentUser.getUserGroup().getNumber() - 1);
        int positionFirstBlock = 0;
        int positionSecondBlock = 0;
        int positionThirdBlock = 0;
        int size;
        if (currentUser.getUserGroup() == LeaderboardGroup.NOT_TOP_50) {
            size = allLeaderboardList.size();
        } else {
            size = allLeaderboardList.size() / 2;
        }

        for (int i = 0; i < size; i++) {
            LeaderboardDto leaderboardDto = allLeaderboardList.get(i);
            if (LeaderboardGroup.TOP_10 == leaderboardDto.getUserGroup()) {
                leaderboardDto.setPositionInCohort(++positionFirstBlock);
                leaderboardFirstBlockMap.putIfAbsent(positionFirstBlock, leaderboardDto);
            } else if (secondBlocLeaderboard == leaderboardDto.getUserGroup()) {
                leaderboardDto.setPositionInCohort(++positionSecondBlock);
                leaderboardSecondBlockMap.putIfAbsent(positionSecondBlock, leaderboardDto);
            } else if (currentUser.getUserGroup() == leaderboardDto.getUserGroup()) {
                leaderboardDto.setPositionInCohort(++positionThirdBlock);
                leaderboardThirdBlockMap.putIfAbsent(positionThirdBlock, leaderboardDto);
            }
        }
        return setSecondAndThirdBlockAndGetLeaderboardResponseDto(currentUser, leaderboardFirstBlockMap, leaderboardSecondBlockMap, leaderboardThirdBlockMap);
    }

    private LeaderboardResponseDto setSecondAndThirdBlockAndGetLeaderboardResponseDto(LeaderboardDto currentUser,
                                                                                      TreeMap<Integer, LeaderboardDto> leaderboardFirstBlock,
                                                                                      TreeMap<Integer, LeaderboardDto> leaderboardSecondBlock,
                                                                                      TreeMap<Integer, LeaderboardDto> leaderboardThirdBlock) {
        log.debug("set second and third block and getting LeaderboardResponseDto , currentUser = {}", currentUser.toString());
        List<LeaderboardPositionDto> secondBlock = new ArrayList<>();
        if (!CollectionUtils.isEmpty(leaderboardSecondBlock)) {
            secondBlock.add(mapLeaderboardRowToDto(leaderboardSecondBlock.lastEntry().getValue(), currentUser));
        }
        List<LeaderboardPositionDto> thirdBlock = new ArrayList<>();
        int lastUser = leaderboardThirdBlock.lastEntry().getKey();
        if (currentUser.getPositionInCohort() == 1) {
            thirdBlock.add(mapLeaderboardRowToDto(currentUser, currentUser));
            thirdBlock.add(mapLeaderboardRowToDto(leaderboardThirdBlock.get(2), currentUser));
        } else if (lastUser == currentUser.getPositionInCohort()) {
            thirdBlock.add(mapLeaderboardRowToDto(leaderboardThirdBlock.get(lastUser - 2), currentUser));
            thirdBlock.add(mapLeaderboardRowToDto(leaderboardThirdBlock.get(lastUser - 1), currentUser));
            thirdBlock.add(mapLeaderboardRowToDto(currentUser, currentUser));
        } else if (currentUser.getPositionInCohort() >= 2) {
            thirdBlock.add(mapLeaderboardRowToDto(leaderboardThirdBlock.get(currentUser.getPositionInCohort() - 1), currentUser));
            thirdBlock.add(mapLeaderboardRowToDto(currentUser, currentUser));
            thirdBlock.add(mapLeaderboardRowToDto(leaderboardThirdBlock.get(currentUser.getPositionInCohort() + 1), currentUser));
        }
        return mapLeaderboardResponseDto(currentUser, leaderboardFirstBlock, leaderboardThirdBlock, secondBlock, thirdBlock);
    }

    private LeaderboardResponseDto mapLeaderboardResponseDto(LeaderboardDto currentUserRow,
                                                             TreeMap<Integer, LeaderboardDto> leaderboardFirstBlockMap,
                                                             TreeMap<Integer, LeaderboardDto> leaderboardThirdBlock,
                                                             List<LeaderboardPositionDto> secondBlock,
                                                             List<LeaderboardPositionDto> thirdBlock) {
        secondBlock.removeIf(Objects::isNull);
        thirdBlock.removeIf(Objects::isNull);
        log.debug("mapping LeaderboardResponseDto");
        return LeaderboardResponseDto.builder()
                .firstBlock(getFirstBlock(leaderboardFirstBlockMap, currentUserRow))
                .secondBlock(secondBlock)
                .thirdBlock(thirdBlock)
                .myCohort(currentUserRow.getUserGroup())
                .myCohortRating(currentUserRow.getPositionInCohort())
                .myCohortUsersCount(leaderboardThirdBlock.size()).build();
    }


    private LeaderboardResponseDto calculateLeaderboardIfUserInTop20(List<LeaderboardDto> allLeaderboardList, LeaderboardDto currentUser) {
        log.debug("calculate leaderboard if user in top 20, Leaderboard size = {} , currentUser = {}", allLeaderboardList.size(), currentUser.toString());
        TreeMap<Integer, LeaderboardDto> leaderboardFirstBlockMap = new TreeMap<>();
        TreeMap<Integer, LeaderboardDto> leaderboardThirdBlockMap = new TreeMap<>();
        int positionFirstBlock = 0;
        int positionThirdBlock = 0;
        for (int i = 0; i <= allLeaderboardList.size() / 2; i++) {
            LeaderboardDto leaderboardDto = allLeaderboardList.get(i);
            if (LeaderboardGroup.TOP_10 == leaderboardDto.getUserGroup()) {
                leaderboardDto.setPositionInCohort(++positionFirstBlock);
                leaderboardFirstBlockMap.putIfAbsent(positionFirstBlock, leaderboardDto);
            } else if (LeaderboardGroup.TOP_20 == leaderboardDto.getUserGroup()) {
                leaderboardDto.setPositionInCohort(++positionThirdBlock);
                leaderboardThirdBlockMap.putIfAbsent(positionThirdBlock, leaderboardDto);
            }
        }
        return setSecondAndThirdBlockAndGetLeaderboardResponseDto(currentUser, leaderboardFirstBlockMap, leaderboardFirstBlockMap, leaderboardThirdBlockMap);
    }

    private List<LeaderboardPositionDto> getFirstBlock(Map<Integer, LeaderboardDto> leaderboardMap, LeaderboardDto currentUserRow) {
        log.debug("getting first block, leaderboardMap size = {} , currentUser = {}", leaderboardMap.size(), currentUserRow.toString());
        return leaderboardMap.values().stream()
                .map(leaderboardDto -> mapLeaderboardRowToDto(leaderboardDto, currentUserRow))
                .limit(3)
                .collect(Collectors.toList());
    }


    public LeaderboardResponseDto calculateLeaderboardIfUserInTop10(List<LeaderboardDto> allLeaderboardList, LeaderboardDto currentUser) {
        log.debug("calculate leaderboard if user in top 10, Leaderboard size = {} , currentUser = {}", allLeaderboardList.size(), currentUser.toString());
        TreeMap<Integer, LeaderboardDto> leaderboardTop10Map = new TreeMap<>();
        int positionFirstBlock = 0;
        for (int i = 0; i <= allLeaderboardList.size() / 2; i++) {
            LeaderboardDto leaderboard = allLeaderboardList.get(i);
            if (LeaderboardGroup.TOP_10 == leaderboard.getUserGroup()) {
                leaderboard.setPositionInCohort(++positionFirstBlock);
                leaderboardTop10Map.putIfAbsent(positionFirstBlock, leaderboard);
            }
        }
        List<LeaderboardPositionDto> thirdBlock = new ArrayList<>();
        int lastUser = leaderboardTop10Map.lastEntry().getKey();
        if (currentUser.getPositionInCohort() == 4) {
            thirdBlock.add(mapLeaderboardRowToDto(currentUser, currentUser));
            thirdBlock.add(mapLeaderboardRowToDto(leaderboardTop10Map.get(5), currentUser));
        } else if (currentUser.getPositionInCohort() >= 5) {
            if (lastUser == currentUser.getPositionInCohort()) {
                thirdBlock.add(mapLeaderboardRowToDto(leaderboardTop10Map.get(lastUser - 2), currentUser));
                thirdBlock.add(mapLeaderboardRowToDto(leaderboardTop10Map.get(lastUser - 1), currentUser));
                thirdBlock.add(mapLeaderboardRowToDto(leaderboardTop10Map.get(lastUser), currentUser));
            } else {
                thirdBlock.add(mapLeaderboardRowToDto(leaderboardTop10Map.get(currentUser.getPositionInCohort() - 1), currentUser));
                thirdBlock.add(mapLeaderboardRowToDto(currentUser, currentUser));
                thirdBlock.add(mapLeaderboardRowToDto(leaderboardTop10Map.get(currentUser.getPositionInCohort() + 1), currentUser));
            }
        }
        return mapLeaderboardResponseDto(currentUser, leaderboardTop10Map, leaderboardTop10Map, Collections.emptyList(), thirdBlock);
    }


    private LeaderboardPositionDto mapLeaderboardRowToDto(LeaderboardDto row, LeaderboardDto currentUser) {
        if (row == null) {
            return null;
        }
        log.debug("mapping LeaderboardRow to dto, row = {}", row);
        return LeaderboardPositionDto.builder()
                .position(row.getRowNumber())
                .cohort(row.getUserGroup())
                .pointsBalance(row.getPoints())
                .currentUser(currentUser != null && currentUser.getKeycloakUserId().equals(row.getKeycloakUserId()))
                .userDto(row.getUserDto())
                .build();
    }

    private LeaderboardDto mapResultOfLeaderboardQueryToLeaderboardDto(Object[] result) {
        log.debug("mapping result of leaderboard query to LeaderboardDto");
        return LeaderboardDto.builder()
                .rowNumber(Integer.parseInt(String.valueOf(result[0])))
                .keycloakUserId(UUID.fromString((String) result[1]))
                .points(Integer.parseInt(String.valueOf(result[2])))
                .userDto(LeaderboardUserDto.builder()
                        .username((String) result[4])
                        .userId(UUID.fromString((String) result[3]))
                        .imageUrl((String) result[5])
                        .build())
                .userGroup(LeaderboardGroup.NOT_TOP_50)
                .build();
    }

    public List<LeaderboardDto> getAllLeaderboard() {
        log.debug("getting all leaderboard from database");
        List<Object[]> allLeaderboardObjects = leaderboardRepository.findAllLeaderboard();
        return allLeaderboardObjects.stream()
                .map(this::mapResultOfLeaderboardQueryToLeaderboardDto)
                .collect(Collectors.toList());

    }


    /**
     * Distribution of users by cohorts if the number of users with POE is less than 12
     *
     * @param allLeaderboardList - all Leaderboard from the database
     * @return a {@link List<LeaderboardDto>}
     */
    public List<LeaderboardDto> calculateAndSetCohortsWhenLess11(List<LeaderboardDto> allLeaderboardList) {
        log.debug("calculate and set cohorts when less 11, Leaderboard size = {}", allLeaderboardList.size());
        int sizeTop50 = (int) (allLeaderboardList.size() * 0.5);
        for (int i = 0; i < sizeTop50; i++) {
            LeaderboardGroup leaderboardGroup = LeaderboardGroup.findByNumber(i);
            allLeaderboardList.get(i).setUserGroup(leaderboardGroup);
        }
        return allLeaderboardList;
    }

    /**
     * Distribution of users by cohorts, if the number of users with POE is more than 12
     *
     * @param allLeaderboardList - all Leaderboard from the database
     * @return a {@link List<LeaderboardDto>}
     */
    public List<LeaderboardDto> calculateAndSetCohortsWhenMore11(List<LeaderboardDto> allLeaderboardList) {
        log.debug("calculate and set cohorts more less 11, Leaderboard size = {}", allLeaderboardList.size());
        int sizeTop50 = (int) (allLeaderboardList.size() * 0.5);
        int euclideanDiv = sizeTop50 % AMOUNT_COHORTS;
        int sets = sizeTop50 / AMOUNT_COHORTS;
        int oneCohort = sets;
        int countLeader = 0;
        for (int j = 0; j < sizeTop50; j++) {
            LeaderboardGroup byNuleaderboardGroup = LeaderboardGroup.findByNumber(countLeader);
            if (oneCohort > 0) {
                allLeaderboardList.get(j).setUserGroup(byNuleaderboardGroup);
                oneCohort--;
            } else {
                if (checkNeedAddAnotherPersonCohort(euclideanDiv, byNuleaderboardGroup)) {
                    allLeaderboardList.get(j).setUserGroup(byNuleaderboardGroup);
                    oneCohort = sets;
                    countLeader++;
                } else {
                    allLeaderboardList.get(j).setUserGroup(LeaderboardGroup.findByNumber(++countLeader));
                    oneCohort = sets - 1;
                }
            }
        }
        return allLeaderboardList;
    }

    private boolean checkNeedAddAnotherPersonCohort(int euclideanDiv, LeaderboardGroup byleaderboardGroup) {
        return (euclideanDiv == 1 && LeaderboardGroup.TOP_50 == byleaderboardGroup) ||
                (euclideanDiv == 2 && (LeaderboardGroup.TOP_50 == byleaderboardGroup || LeaderboardGroup.TOP_30 == byleaderboardGroup)) ||
                (euclideanDiv == 3 && (LeaderboardGroup.TOP_50 == byleaderboardGroup || LeaderboardGroup.TOP_40 == byleaderboardGroup || LeaderboardGroup.TOP_20 == byleaderboardGroup)) ||
                (euclideanDiv == 4 && LeaderboardGroup.TOP_10 != byleaderboardGroup);
    }

    public String calculateCurrentUserCohort() {
        UUID keycloakUserId = securityUtil.getCurrentUserId();
        return getAllLeaderboardListAndSetCohort().stream()
                .filter(r -> r.getKeycloakUserId().equals(keycloakUserId))
                .findFirst()
                .map(leaderboardDto -> leaderboardDto.getUserGroup().toString())
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
    }
}
