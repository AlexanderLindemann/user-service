package com.nft.platform.controller;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.common.enums.BundleType;
import com.nft.platform.dto.request.PurchaseForCoinsRequestDto;
import com.nft.platform.keycloak.WithMockKeycloakToken;
import com.nft.platform.repository.BundleForCoinsRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.nft.platform.util.security.RoleConstants.ROLE_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/celebrity.sql")
@Sql("classpath:sql/user_profile.sql")
@Sql("classpath:sql/profile_wallet.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PurchaseForCoinsControllerTest extends AbstractIntegrationTest {
    private final static String BASE_PATH = "/api/v1/bundle-for-coins";
    private final static String USER_ID = "c7e296ae-3be2-4e02-9af8-1031941ceb19";
    private final static UUID USER_UID = UUID.fromString("c7e296ae-3be2-4e02-9af8-1031941ceb19");
    private final static UUID TECH_CELEB_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    BundleForCoinsRepository bundleForCoinsRepository;

    @Autowired
    ProfileWalletRepository profileWalletRepository;

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_USER})
    public void testByBundleForCoins() throws Exception {
        var bundle = bundleForCoinsRepository.findByTypeAndBundleSize(BundleType.NFT_VOTE, 1).orElseThrow();
        var body = new PurchaseForCoinsRequestDto(bundle.getId(), TECH_CELEB_ID);
        var walletBeforePurchase = profileWalletRepository.findByKeycloakUserIdAndCelebrityId(USER_UID, TECH_CELEB_ID).orElseThrow();

        mockMvc.perform(post(BASE_PATH + "/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        var walletAfterPurchase = profileWalletRepository.findByKeycloakUserIdAndCelebrityId(USER_UID, TECH_CELEB_ID).orElseThrow();

        Assertions.assertNotEquals(walletBeforePurchase.getCoinBalance(), walletAfterPurchase.getCoinBalance());
    }
}