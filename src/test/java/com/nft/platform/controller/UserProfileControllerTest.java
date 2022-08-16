package com.nft.platform.controller;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProfileControllerTest extends AbstractIntegrationTest {

    private final static String BASE_PATH = "/api/v1/user-profiles";

    private final static UUID PIZZA_MAKER_USER_ID = UUID.fromString("72048ea2-598c-43be-a6ee-d9980a1d60fe");

    private final static UUID NON_EXISTING_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private UserProfileService userProfileService;

    @Test
    public void findOtherUserById_404() throws Exception {
        mvc.perform(get(BASE_PATH + "/" + NON_EXISTING_USER_ID + "/poor"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findOtherUser_500() throws Exception {
        doThrow(new RuntimeException()).when(userProfileService).findPoorUserProfile(PIZZA_MAKER_USER_ID);
    }
}
