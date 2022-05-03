package com.nft.platform.dto.poe.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "UserIdActivityBalancePositionResponseDto Response DTO")
public class UserIdActivityBalancePositionResponseDto {

    @Schema(required = true, description = "User UUID from Keycloak")
    @NotNull
    private UUID keycloakUserId;

    @Schema(description = "Activity Balance")
    private Integer activityBalance;

    @Schema(description = "Position")
    private Integer position;
}
