package com.nft.platform.feign.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WalletInfoResponseDto {

    @Schema(description = "isSolana")
    private Boolean isSolana;
}
