package com.nft.platform.service;

import com.nft.platform.AbstractIntegrationTest;
import com.nft.platform.dto.CelebrityFanDto;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FanServiceTest extends AbstractIntegrationTest {

    @Test
    public void testCreateTopFansForEmptyList() {
        assertNotNull(FanService.createTopFansDto(Collections.emptyList(), 1, 1));
    }

    @Test
    public void testCreateTopFansForTwoItems() {
        var testFans = List.of(
                CelebrityFanDto.builder()
                        .avatar("FIRST")
                        .top(1)
                        .token(BigDecimal.valueOf(100))
                        .build(),
                CelebrityFanDto.builder()
                        .avatar("SECOND")
                        .top(2)
                        .token(BigDecimal.valueOf(50))
                        .build()
        );

        var res = FanService.createTopFansDto(testFans, 2, 2);
        assertEquals("FIRST", res.getFans().get(0).getAvatar());
        assertEquals("SECOND", res.getFans().get(1).getAvatar());
        assertEquals(BigDecimal.valueOf(150), res.getFansSum().getToken());
    }

}