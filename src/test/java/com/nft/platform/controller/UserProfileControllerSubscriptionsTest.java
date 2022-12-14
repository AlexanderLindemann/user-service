package com.nft.platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.keycloak.WithMockKeycloakToken;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.nft.platform.util.security.RoleConstants.ROLE_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/celebrity.sql")
@Sql("classpath:sql/user_profile.sql")
@Sql("classpath:sql/profile_wallet.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserProfileControllerSubscriptionsTest extends AbstractIntegrationTest {
    private final static String BASE_PATH = "/api/v1/user-profiles";
    private final static String USER_ID = "c7e296ae-3be2-4e02-9af8-1031941ceb19";

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testGetSubscribedСelebrities() throws Exception {
        var resp = mockMvc.perform(get(BASE_PATH + "/me/celebrities/subscribed"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn()
                .getResponse();
        var celebrities = getBody(resp.getContentAsString(), new TypeReference<List<CelebrityResponseDto>>() {
        });
        Assert.assertEquals(1, celebrities.size());
        var c = celebrities.get(0);
        Assert.assertEquals("Cyrille", c.getName());
        Assert.assertTrue(c.isSubscribed());
    }

    @Test
    @Sql(statements = "delete from profile_wallet")
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testGetSubscribedСelebritiesIfUserWithoutSubscriptions() throws Exception {
        var resp = mockMvc.perform(get(BASE_PATH + "/me/celebrities/subscribed"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn()
                .getResponse();
        var celebrities = getBody(resp.getContentAsString(), new TypeReference<List<CelebrityResponseDto>>() {
        });
        Assert.assertTrue(celebrities.isEmpty());
    }

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testGetNotSubscribedСelebrities() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/me/celebrities/unsubscribed"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content[0].name").value("Vlado"))
                .andExpect(jsonPath("$.content[0].subscribed").value("false"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @Sql(statements = "delete from profile_wallet")
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testGetNotSubscribedСelebritiesIfUserWithoutSubscriptions() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/me/celebrities/unsubscribed"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
