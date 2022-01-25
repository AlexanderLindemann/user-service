package com.nft.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.mapper.UserProfileMapper;
import com.nft.platform.service.UserProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserProfileControllerTest {

    private final static String BASE_PATH = "/api/v1/user-profiles";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private UserProfileMapper userProfileMapper;

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
