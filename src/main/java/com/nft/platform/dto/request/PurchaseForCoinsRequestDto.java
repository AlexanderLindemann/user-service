package com.nft.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bundle for Coins Request DTO")
public class PurchaseForCoinsRequestDto {

    @Schema(required = true)
    private UUID bundleForCoinsId;

}
