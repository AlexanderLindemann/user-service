package com.nft.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nft.platform.common.util.JsonUtil;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.PoorUserProfileResponseDto;
import com.nft.platform.service.UserProfileService;
import com.nft.platform.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProfileControllerTest extends AbstractIntegrationTest {

    private final static String BASE_PATH = "/api/v1/user-profiles";

    private final static UUID PIZZA_MAKER_USER_ID = UUID.fromString("72048ea2-598c-43be-a6ee-d9980a1d60fe");

    private final static UUID NON_EXISTING_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private UserProfileService userProfileService;

//    @Test
    //TODO Add DBRider
    public void findOtherUserById_200() throws Exception {
        //given
        PoorUserProfileResponseDto expectedResult = JsonUtil.getObjectFromJson("json/poorUserProfileResponseDto.json", PoorUserProfileResponseDto.class);

        //when
        mvc.perform(get(BASE_PATH + "/" + PIZZA_MAKER_USER_ID + "/poor"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(expectedResult.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(expectedResult.getLastName()))
                .andExpect(jsonPath("$[2].nickname").value(expectedResult.getNickname()))
                .andExpect(jsonPath("$[3].description").value(expectedResult.getDescription()))
                .andExpect(jsonPath("$[4].site").value(expectedResult.getSite()))
                .andExpect(jsonPath("$[5].facebookId").value(expectedResult.getFacebookId()))
                .andExpect(jsonPath("$[6].twitterId").value(expectedResult.getTwitterId()))
                .andExpect(jsonPath("$[7].googleId").value(expectedResult.getGoogleId()))
                .andExpect(jsonPath("$[8].imageUrl").value(expectedResult.getImageUrl()))
                .andExpect(jsonPath("$[9].imagePromoBannerUrl").value(expectedResult.getImagePromoBannerUrl()))
                .andExpect(jsonPath("$[10].createdAt").value(expectedResult.getCreatedAt()));

    }

    @Test
    public void findOtherUserById_404() throws Exception {
        mvc.perform(get(BASE_PATH + "/" + NON_EXISTING_USER_ID + "/poor"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findOtherUser_500() throws Exception {
        doThrow(new RuntimeException()).when(userProfileService).findPoorUserProfile(PIZZA_MAKER_USER_ID);
    }


    //    @Test
    @DisplayName("Test Get User Profile Page")
    void getUserProfilePage() throws Exception {
        mvc.perform(get(BASE_PATH))
                .andExpect(status().isOk());
    }

    //    @Test
    @DisplayName("Test Update User Profile")
    void updateUserProfile() throws Exception {
        UserProfileRequestDto requestDto = UserProfileRequestDto.builder()
                .keycloakUserId(UUID.randomUUID())
                .build();
        mvc.perform(put(BASE_PATH + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    //    @Test
    @DisplayName("Test Create User Profile")
    void createUserProfile() throws Exception {
        UserProfileRequestDto requestDto = UserProfileRequestDto.builder()
                .keycloakUserId(UUID.randomUUID())
                .build();
        mvc.perform(put(BASE_PATH + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}
