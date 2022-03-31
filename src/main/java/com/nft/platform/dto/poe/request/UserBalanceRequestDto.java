package com.nft.platform.dto.poe.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Balance Request DTO")
public class UserBalanceRequestDto {

    @Schema(description = "Keycloak User Id", required = true)
    private UUID userId;

    @Schema(description = "Celebrity Id", required = true)
    private UUID celebrityId;

    @Schema(description = "Period Id")
    private UUID periodId;
}
