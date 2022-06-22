package com.nft.platform.service;

import com.nft.platform.dto.CelebrityFanDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FanServiceTest {

    @Test
    public void testCreateTopFansForEmptyList() {
        Assert.assertNotNull(FanService.createTopFansDto(Collections.emptyList(), 1, 1));
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
        Assert.assertEquals("FIRST", res.getFans().get(0).getAvatar());
        Assert.assertEquals("SECOND", res.getFans().get(1).getAvatar());
        Assert.assertEquals(BigDecimal.valueOf(150), res.getFansSum().getToken());
    }
}