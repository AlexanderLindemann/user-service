package com.nft.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Schema(description = "A celebrity's fan")
public class CelebrityFanDto {
    UUID id;
    int top;
    String avatar;
    BigDecimal token;
    @Nullable
    Double usdt;
}
