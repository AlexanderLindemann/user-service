package com.nft.platform.controller;

import com.nft.platform.domain.LeaderboardDto;
import com.nft.platform.dto.enums.LeaderboardGroup;
import com.nft.platform.service.LeaderboardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class LeaderboardServiceUnitTest {


    @InjectMocks
    LeaderboardService leaderboardService;

    @Test
    void calculateAndSetCohortTest() {
        List<LeaderboardDto> leaderboardDtoList = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            leaderboardDtoList.add(LeaderboardDto.builder().keycloakUserId(UUID.randomUUID())
                    .rowNumber(i)
                    .userGroup(LeaderboardGroup.NOT_TOP_50)
                    .points(ThreadLocalRandom.current().nextInt(0, 1000 + 1)).build());

        }
        List<LeaderboardDto> resultList = leaderboardService.calculateAndSetCohort(leaderboardDtoList);
        assertNotNull(resultList);
        assertEquals(leaderboardDtoList.size(), resultList.size());
        assertEquals(LeaderboardGroup.TOP_10,leaderboardDtoList.get(0).getUserGroup() );
        assertEquals(LeaderboardGroup.TOP_20,leaderboardDtoList.get(1).getUserGroup());
        assertEquals(LeaderboardGroup.TOP_30,leaderboardDtoList.get(2).getUserGroup());
        assertEquals(LeaderboardGroup.NOT_TOP_50,leaderboardDtoList.get(3).getUserGroup());
    }
}
