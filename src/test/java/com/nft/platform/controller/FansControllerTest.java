package com.nft.platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.dto.CelebrityFanDto;
import com.nft.platform.keycloak.WithMockKeycloakToken;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.nft.platform.util.security.RoleConstants.ROLE_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/celebrity.sql")
@Sql("classpath:sql/user_profile.sql")
@Sql("classpath:sql/profile_wallet.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class FansControllerTest extends AbstractIntegrationTest {
    private final static String BASE_PATH = "/api/v1/celebrity/fans";
    private final static String USER_ID = "c7e296ae-3be2-4e02-9af8-1031941ceb19";
    private static final String CELEBRITY_ID = "d212e77f-3057-4db5-80f2-14d52d4dae35";

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testGetTopFans() throws Exception {
        var resp = mockMvc.perform(get(BASE_PATH + "/top")
                .param("celebrityId", CELEBRITY_ID))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn()
                .getResponse();
        var top = getBody(resp.getContentAsString(), new TypeReference<List<CelebrityFanDto>>() {
        });
        Assert.assertEquals(4, top.size());
    }
}
