package com.nft.platform.controller;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.common.enums.Blockchain;
import com.nft.platform.dto.request.CryptoWalletRequestDto;
import com.nft.platform.keycloak.WithMockKeycloakToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static com.nft.platform.util.security.RoleConstants.ROLE_ADMIN_CELEBRITY;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/user_profile.sql")
@Sql("classpath:dataset/controller/cryptowalletController/get-cryptowallets.sql")
@Sql(value = "classpath:dataset/controller/cryptowalletController/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CryptoWalletControllerTest extends AbstractIntegrationTest {

    private final String BASE_PATH = "/api/v1/crypto-wallet";

    private final static String USER_ID = "c1e296ae-3be2-4e02-9af8-1031941ceb19";

    @BeforeEach
    public void setUp() {
        //doNothing().when(tmpFanTokenDistributionEventHandler).handle(any());
        SolanaAdapterMocks.setGetWalletInfo(wireMockServer, true);
    }

    @Test
    @WithMockKeycloakToken(id = "f9894b9c-3e3a-4eb5-96d2-3174921018be", roles = {ROLE_ADMIN_CELEBRITY})
    public void mustReturnAllCryptowallets() throws Exception {
        mockMvc.perform(get(BASE_PATH))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_ADMIN_CELEBRITY})
    public void mustReturnListOfCryptowalletsBasedOnUserProfileId() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/by-user-profile/{userProfileId}", "4e2e4823-6ead-41e7-a04b-2caa138618f9"))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$[0].id").value("5acc121f-add2-4daa-abb9-2ade506ceb51"))
            .andExpect(jsonPath("$[0].defaultWallet").value(false))
            .andExpect(jsonPath("$[0].externalCryptoWalletId").value("2eM7moZpiHVigsZAHaYX9feYGhM4ArPKm8yzCfaBfP1M"))
            .andExpect(jsonPath("$[1].id").value("53ee047d-91bf-4dc6-a9e9-192a40dace5f"))
            .andExpect(jsonPath("$[1].defaultWallet").value(false))
            .andExpect(jsonPath("$[1].externalCryptoWalletId").value("GoqoZEw24WLHB3GV4zb1t88Hcmt39AwLvSiDTwUUN6w9"))
            .andExpect(jsonPath("$[2].id").value("92280253-ec21-44cc-b36a-8cbbff1fbb10"))
            .andExpect(jsonPath("$[2].defaultWallet").value(false))
            .andExpect(jsonPath("$[2].externalCryptoWalletId").value("BPQ8mbS8FEA8kwKESeT7UFU39hujVixaRyvr1hqrbu3n"))
            .andExpect(jsonPath("$[3].id").value("851b3618-ffba-4d96-b6d0-7286423b82dd"))
            .andExpect(jsonPath("$[3].defaultWallet").value(true))
            .andExpect(jsonPath("$[3].externalCryptoWalletId").value("HhDXPvWqF456fd1GVjUu8VKrVeVkjwnTSnjed3yycccN"));
    }

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_ADMIN_CELEBRITY})
    public void mustReturnListOfCryptowalletsBasedOnKeyCloakId() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/by-user-keycloak/{userKeycloakId}", "f9894b9c-3e3a-4eb5-96d2-3174921018be"))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$[0].id").value("5acc121f-add2-4daa-abb9-2ade506ceb51"))
            .andExpect(jsonPath("$[0].defaultWallet").value(false))
            .andExpect(jsonPath("$[0].externalCryptoWalletId").value("2eM7moZpiHVigsZAHaYX9feYGhM4ArPKm8yzCfaBfP1M"))
            .andExpect(jsonPath("$[1].id").value("53ee047d-91bf-4dc6-a9e9-192a40dace5f"))
            .andExpect(jsonPath("$[1].defaultWallet").value(false))
            .andExpect(jsonPath("$[1].externalCryptoWalletId").value("GoqoZEw24WLHB3GV4zb1t88Hcmt39AwLvSiDTwUUN6w9"))
            .andExpect(jsonPath("$[2].id").value("92280253-ec21-44cc-b36a-8cbbff1fbb10"))
            .andExpect(jsonPath("$[2].defaultWallet").value(false))
            .andExpect(jsonPath("$[2].externalCryptoWalletId").value("BPQ8mbS8FEA8kwKESeT7UFU39hujVixaRyvr1hqrbu3n"))
            .andExpect(jsonPath("$[3].id").value("851b3618-ffba-4d96-b6d0-7286423b82dd"))
            .andExpect(jsonPath("$[3].defaultWallet").value(true))
            .andExpect(jsonPath("$[3].externalCryptoWalletId").value("HhDXPvWqF456fd1GVjUu8VKrVeVkjwnTSnjed3yycccN"));
    }

    // TODO does not work buy some reason;

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_ADMIN_CELEBRITY})
    public void mustSuccesfullyCreateNewCryptowallet() throws Exception {

        mockMvc.perform(post("/api/v1/crypto-wallet")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(
                CryptoWalletRequestDto.builder()
                    .externalCryptoWalletId("1BoatSLRHtKNngkdXEeobR76b53LETtpyT")
                    .defaultWallet(true)
                    .blockchain(Blockchain.SOLANA)
                .build()
            )))
            .andExpect(status().is(HttpStatus.CREATED.value()))
            .andExpectAll(
                jsonPath("$.id").exists(),
                jsonPath("$.externalCryptoWalletId").value("1BoatSLRHtKNngkdXEeobR76b53LETtpyT"),
                jsonPath("$.blockchain").value(Blockchain.SOLANA.toString()),
                jsonPath("$.defaultWallet").value(false)
            );

    }

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_ADMIN_CELEBRITY})
    public void mustSuccesfullyUpdateCryptowallet() throws Exception {

        mockMvc.perform(put("/api/v1/crypto-wallet/{id}/default", "fbeadb51-ff77-4e3e-a5ee-594f5bb92b2e")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpectAll(jsonPath("$.id").exists());

    }

    @Test
    @WithMockKeycloakToken(id = USER_ID, roles = {ROLE_ADMIN_CELEBRITY})
    public void mustSuccesfullyDeleteCryptowallet() throws Exception {

        mockMvc.perform(delete("/api/v1/crypto-wallet/{id}", "fbeadb51-ff77-4e3e-a5ee-594f5bb92b2e")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(HttpStatus.OK.value()));

    }

}