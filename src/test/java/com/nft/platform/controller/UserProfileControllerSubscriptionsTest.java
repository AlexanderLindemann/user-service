package com.nft.platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.keycloak.WithMockKeycloakToken;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.nft.platform.util.security.RoleConstants.ROLE_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
                .andReturn()
                .getResponse();
        var celebrities = getBody(resp.getContentAsString(), new TypeReference<List<CelebrityResponseDto>>() {
        });
        Assert.assertEquals(1, celebrities.size());
        var c = celebrities.get(0);
        Assert.assertEquals("Cyrille", c.getName());
    }

    @Test
    @Sql(statements = "delete from profile_wallet")
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testGetSubscribedСelebritiesIfUserWithoutSubscriptions() throws Exception {
        var resp = mockMvc.perform(get(BASE_PATH + "/me/celebrities/subscribed"))
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
                .andExpect(jsonPath("$.content[0].name").value("TECH_CELEB"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @Sql(statements = "delete from profile_wallet")
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testGetNotSubscribedСelebritiesIfUserWithoutSubscriptions() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/me/celebrities/unsubscribed"))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
