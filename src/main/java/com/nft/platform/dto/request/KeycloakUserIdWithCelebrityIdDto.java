package com.nft.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@Schema(description = "KeycloakUserId and Celebrity Id params")
public class KeycloakUserIdWithCelebrityIdDto {
    @Schema(description = "Celebrity Id", required = true)
    @NotNull
    private UUID celebrityId;

    @Schema(description = "Keycloak User Id", required = true)
    @NotNull
    private UUID keycloakUserId;
}
