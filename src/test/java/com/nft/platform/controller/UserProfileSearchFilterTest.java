package com.nft.platform.controller;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.keycloak.WithMockKeycloakToken;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static com.nft.platform.util.security.RoleConstants.ROLE_TECH_TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/user_profile.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserProfileSearchFilterTest extends AbstractIntegrationTest {
    private final static String BASE_PATH = "/api/v1/user-profiles";
    private final static String USER_ID = "c7e296ae-3be2-4e02-9af8-1031941ceb19";

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_TECH_TOKEN})
    public void testSearchByEmailUpperCase() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search").param("email", "AUTH@gmail.com"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].email").value("auth@gmail.com"));
    }

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_TECH_TOKEN})
    public void testSearchByPhone() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search").param("phone", "9198746734"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].email").value("auth@gmail.com"));
    }

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_TECH_TOKEN})
    public void testSearchByUserName() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/search").param("name", "AUTH@gmail.com"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$[0].email").value("auth@gmail.com"));
    }
}
