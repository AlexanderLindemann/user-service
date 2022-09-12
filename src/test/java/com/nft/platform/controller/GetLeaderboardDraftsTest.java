package com.nft.platform.controller;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.dto.enums.LeaderboardGroup;
import com.nft.platform.dto.response.LeaderboardPositionDto;
import com.nft.platform.keycloak.WithMockKeycloakToken;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.nft.platform.util.security.RoleConstants.ROLE_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(value = "classpath:dataset/controller/leaderboardController/clear-table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:dataset/controller/leaderboardController/get-period.sql",
        "classpath:dataset/controller/leaderboardController/get-user-points.sql",
        "classpath:dataset/controller/leaderboardController/get-user-profile.sql",
        "classpath:dataset/controller/leaderboardController/refresh-materialized-viev.sql"})
@Sql(value = "classpath:dataset/controller/leaderboardController/clear-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GetLeaderboardDraftsTest extends AbstractIntegrationTest {

    public static final String LEADERBOARD_ALL = "/api/v1/leaderboard";
    public static final String LEADERBOARD_COHORT = "/api/v1/leaderboard/cohort";
    private static final String LEADERBOARD_USER_ID = "/api/v1/leaderboard/{userId}";
    private static final UUID UUID_FOUND = UUID.fromString("b620e2a8-1895-49cc-b30a-64aaf9943319");
    private static final UUID UUID_NOT_FOUND = UUID.fromString("64817d06-fa36-4403-b22b-da004ec991ba");
    private static final String CURRENT_USER_ID = "0ab100b0-ffc7-4035-b1aa-2689399b92c1";

    @Test
    @WithMockKeycloakToken(id = CURRENT_USER_ID)
    void getAllLeaderIfCurrentUserInTop30_200() throws Exception {
        mockMvc.perform(get(LEADERBOARD_ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstBlock[0].cohort").value(LeaderboardGroup.TOP_10.toString()))
                .andExpect(jsonPath("$.firstBlock[0].position").value(1))
                .andExpect(jsonPath("$.firstBlock[0].currentUser").value(false))
                .andExpect(jsonPath("$.firstBlock[0].pointsBalance").value(1700000))
                .andExpect(jsonPath("$.firstBlock[0].userDto.userId").value("79c6cdd0-4c60-4f54-9c76-ef955e2927ff"))
                .andExpect(jsonPath("$.firstBlock.size()").value(3))
                .andExpect(jsonPath("$.secondBlock[0].cohort").value(LeaderboardGroup.TOP_20.toString()))
                .andExpect(jsonPath("$.secondBlock[0].position").value(11))
                .andExpect(jsonPath("$.secondBlock[0].currentUser").value(false))
                .andExpect(jsonPath("$.secondBlock[0].pointsBalance").value(1257))
                .andExpect(jsonPath("$.secondBlock[0].userDto.userId").value("634a647c-5063-47d7-9528-01eeed9237b9"))
                .andExpect(jsonPath("$.secondBlock.size()").value(1))
                .andExpect(jsonPath("$.thirdBlock[1].cohort").value(LeaderboardGroup.TOP_30.toString()))
                .andExpect(jsonPath("$.thirdBlock[1].position").value(15))
                .andExpect(jsonPath("$.thirdBlock[1].currentUser").value(true))
                .andExpect(jsonPath("$.thirdBlock[1].pointsBalance").value(360))
                .andExpect(jsonPath("$.thirdBlock[1].userDto.userId").value("a0d7529a-37f9-4e4a-ae6b-f660a301d134"))
                .andExpect(jsonPath("$.thirdBlock.size()").value(3))
                .andExpect(jsonPath("$.myCohort").value(LeaderboardGroup.TOP_30.toString()))
                .andExpect(jsonPath("$.myCohortRating").value(4))
                .andExpect(jsonPath("$.myCohortUsersCount").value(5));
    }

    @Test
    void getAllLeaderIfCurrentUnauthorized_200() throws Exception {
        List<LeaderboardPositionDto> secondAndThirdBlock = new ArrayList<>();
        mockMvc.perform(get(LEADERBOARD_ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstBlock[0].cohort").value(LeaderboardGroup.TOP_10.toString()))
                .andExpect(jsonPath("$.firstBlock[0].position").value(1))
                .andExpect(jsonPath("$.firstBlock[0].currentUser").value(false))
                .andExpect(jsonPath("$.firstBlock[0].pointsBalance").value(1700000))
                .andExpect(jsonPath("$.firstBlock[0].userDto.userId").value("79c6cdd0-4c60-4f54-9c76-ef955e2927ff"))
                .andExpect(jsonPath("$.firstBlock.size()").value(5))
                .andExpect(jsonPath("$.secondBlock").value(secondAndThirdBlock))
                .andExpect(jsonPath("$.thirdBlock").value(secondAndThirdBlock))
                .andExpect(jsonPath("$.myCohort").doesNotExist())
                .andExpect(jsonPath("$.myCohortRating").value(0))
                .andExpect(jsonPath("$.myCohortUsersCount").value(0));
    }


    @Test
    void getLeaderboardByIdIfCurrentUnauthorized_200() throws Exception {
        mockMvc.perform(get(LEADERBOARD_USER_ID, UUID_FOUND))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otherUser.position").value(2))
                .andExpect(jsonPath("$.otherUser.pointsBalance").value(880000))
                .andExpect(jsonPath("$.otherUser.user.userId").value(UUID_FOUND.toString()))
                .andExpect(jsonPath("$.otherUser.user.username").value("griekat+20@gmail.com"))
                .andExpect(jsonPath("$.otherUser.user.imageUrl").value("https://nft-platform.s3.eu-central-1.amazonaws.com/avatars/5d7d3fac-8f1f-4b85-8a2d-81fb89f90965"))
                .andExpect(jsonPath("$.currentUser").doesNotExist());
    }

    @Test
    @WithMockKeycloakToken(id = CURRENT_USER_ID)
    void getLeaderboardById_200() throws Exception {
        mockMvc.perform(get(LEADERBOARD_USER_ID, UUID_FOUND))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otherUser.position").value(2))
                .andExpect(jsonPath("$.otherUser.pointsBalance").value(880000))
                .andExpect(jsonPath("$.otherUser.user.userId").value(UUID_FOUND.toString()))
                .andExpect(jsonPath("$.otherUser.user.username").value("griekat+20@gmail.com"))
                .andExpect(jsonPath("$.otherUser.user.anonymous").value(false))
                .andExpect(jsonPath("$.currentUser.position").value(15))
                .andExpect(jsonPath("$.currentUser.pointsBalance").value(360))
                .andExpect(jsonPath("$.currentUser.user.userId").value("a0d7529a-37f9-4e4a-ae6b-f660a301d134"))
                .andExpect(jsonPath("$.currentUser.user.username").value("Burro_Eeyoure"))
                .andExpect(jsonPath("$.currentUser.user.anonymous").value(false));
    }

    @Test
    void getLeaderboardById_404() throws Exception {
        mockMvc.perform(get(LEADERBOARD_USER_ID, UUID_NOT_FOUND))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockKeycloakToken(id = CURRENT_USER_ID, roles = {ROLE_USER})
    void getCurrentUserCohort_200() throws Exception {
        mockMvc.perform(get(LEADERBOARD_COHORT))
                .andExpect(status().isOk())
                .andExpect(content().string(LeaderboardGroup.TOP_30.toString()));
    }

    @Test
    @WithMockKeycloakToken(roles = {ROLE_USER})
    void getCurrentUserCohort_404() throws Exception {
        mockMvc.perform(get(LEADERBOARD_COHORT))
                .andExpect(status().isNotFound());
    }

}
