package com.nft.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "UserRewardIncreaseDto")
public class UserRewardIncreaseDto {

    @Schema(description = "Celebrity Id", required = true)
    @NotNull
    private UUID celebrityId;

    @Schema(description = "Keycloak User Id", required = true)
    @NotNull
    private UUID keycloakUserId;

    @Schema(description = "Amount of Reward", required = true)
    @Positive
    @NotNull
    private Integer amount;

}