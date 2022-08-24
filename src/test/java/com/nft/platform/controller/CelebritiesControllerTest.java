package com.nft.platform.controller;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.keycloak.WithMockKeycloakToken;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static com.nft.platform.util.security.RoleConstants.ROLE_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/celebrity.sql")
@Sql("classpath:sql/user_profile.sql")
@Sql("classpath:sql/profile_wallet.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CelebritiesControllerTest extends AbstractIntegrationTest {
    private final static String BASE_PATH = "/api/v1/celebrity";
    private final static String USER_WITHOUT_SUBSCRIPTIONS_ID = "c1e296ae-3be2-4e02-9af8-1031941ceb19";
    private final static String USER_WITH_SUBSCRIPTIONS_ID = "7649b847-3bb4-458a-9c9c-064e65b0f427";

    @Test
    @WithMockKeycloakToken(id = USER_WITHOUT_SUBSCRIPTIONS_ID, roles = {ROLE_USER})
    public void testGetCelebritiesPage() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content[0].id").value("25839f9e-a084-482c-82b7-8defe5fb6b62"));
    }

    @Test
    @WithMockKeycloakToken(id = USER_WITH_SUBSCRIPTIONS_ID, roles = {ROLE_USER})
    public void testGetCelebritiesPageWithoutSubscriptions() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content[0].id").value("25839f9e-a084-482c-82b7-8defe5fb6b62"));
    }
}
