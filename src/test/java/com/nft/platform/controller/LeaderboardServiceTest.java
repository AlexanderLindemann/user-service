package com.nft.platform.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class LeaderboardServiceTest extends AbstractIntegrationTest {

    private static final String URL = "/api/v1/leaderboard/{userId}";
    private static final UUID uuid = UUID.fromString("64817d06-fa36-4403-b22b-da004ec881ba");
    private static final UUID uuid_not_found = UUID.fromString("64817d06-fa36-4403-b22b-da004ec991ba");
    @MockBean
    private EntityManager entityManager;
    @SpyBean
    private UserProfileRepository userProfileRepository;

    @Test
    @DataSet(value = "dataset/UserProfile.json")
    void getLeaderboardById_200() throws Exception {
        var db = userProfileRepository.findByKeycloakUserId(uuid).get();

        Object[] responseObject = {777, uuid.toString(), 888, null};
        List<Object[]> responseList = new ArrayList<>();
        responseList.add(responseObject);
        mockEntityManager(responseList);

        mockMvc.perform(get(URL, uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otherUser.position").value(responseObject[0]))
                .andExpect(jsonPath("$.otherUser.pointsBalance").value(responseObject[2]))
                .andExpect(jsonPath("$.otherUser.userDto.keycloakUserId").value(responseObject[1]))
                .andExpect(jsonPath("$.otherUser.userDto.keycloakUserId").value(responseObject[1]))
                .andExpect(jsonPath("$.otherUser.userDto.username").value(db.getUsername()))
                .andExpect(jsonPath("$.otherUser.userDto.imageUrl").value(db.getImageUrl()));
    }

    private void mockEntityManager(List<Object[]> responseList) {
        Query mockedQuery = mock(Query.class);
        Mockito.when(mockedQuery.getResultList()).thenReturn(responseList);
        Mockito.when(mockedQuery.setParameter(anyString(), any())).thenReturn(mockedQuery);
        Mockito.when(entityManager.createNativeQuery(any())).thenReturn(mockedQuery);
    }

    @Test
    void getLeaderboardById_404() throws Exception {
        mockEntityManager(Collections.emptyList());
        mockMvc.perform(get(URL, uuid_not_found))
                .andExpect(status().isNotFound());
    }

}