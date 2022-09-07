package com.nft.platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.common.util.JsonUtil;
import com.nft.platform.dto.response.CelebrityResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:sql/getPopular.sql")
@Sql(value = "classpath:sql/clear_all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SuppressWarnings("unchecked")
public class GetPopularTest extends AbstractIntegrationTest {

    private static final String URL_200 = "/api/v1/celebrity/popular";
    private static final UUID TOP_CELEBRITY_1 = UUID.fromString("d212e77f-3057-4db5-80f2-14d52d4dae35");
    private static final UUID TOP_CELEBRITY_2 = UUID.fromString("12504d96-6bb7-4ead-bd1f-a33fccfed97f");
    private static final UUID TOP_CELEBRITY_3 = UUID.fromString("b5ecb411-a2a8-4a72-b9ab-3a64d8c70120");
    private static final int TOP_COUNT_NFT_1 = 47;
    private static final int TOP_COUNT_NFT_2 = 7;
    private static final int TOP_COUNT_NFT_3 = 4;
    private static final String TOP_1_NAME = "Cyrille";
    private static final String TOP_1_LAST_NAME = "David";
    private static final String TOP_2_NAME = "Hussain";
    private static final String TOP_2_LAST_NAME = "Al Jassmi";
    private static final String TOP_3_NAME = "Lady";
    private static final String TOP_3_LAST_NAME = "Natalii";

    private static final Map<UUID, Integer> TOP_CELEBRITY_BY_NFT_COUNT_MAP = Map.of(TOP_CELEBRITY_1, TOP_COUNT_NFT_1,
            TOP_CELEBRITY_2, TOP_COUNT_NFT_2, TOP_CELEBRITY_3, TOP_COUNT_NFT_3);

    @Test
    public void getPopular_200() throws Exception {
        // init integration with nft-service
        NftServiceMocks.setTopCelebrityByNftCountMap(wireMockServer, TOP_CELEBRITY_BY_NFT_COUNT_MAP);
        // get top celebrities
        String content = mockMvc.perform(get(URL_200))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<CelebrityResponseDto> resultList = objectMapper.readValue(content, new TypeReference<>() {
        });

        assertEquals(resultList.get(0).getName(), TOP_1_NAME);
        assertEquals(resultList.get(0).getLastName(), TOP_1_LAST_NAME);
        assertEquals(resultList.get(1).getName(), TOP_2_NAME);
        assertEquals(resultList.get(1).getLastName(), TOP_2_LAST_NAME);
        assertEquals(resultList.get(2).getName(), TOP_3_NAME);
        assertEquals(resultList.get(2).getLastName(), TOP_3_LAST_NAME);
    }

    @Test
    public void getPopular_500() throws Exception {
        //given
        doThrow(new RuntimeException()).when(celebrityService).getPopular();
        //when
        mockMvc.perform(get(URL_200))
                //then
                .andExpect(status().isInternalServerError());
    }

}
