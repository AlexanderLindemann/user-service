package com.nft.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Schema(description = "A celebrity's fan")
@NoArgsConstructor
@AllArgsConstructor
public class CelebrityFanDto {
    UUID id;
    int top;
    String avatar;
    BigDecimal token;
    @Nullable
    Double usdt;
}
