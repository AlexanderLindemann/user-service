package com.nft.platform.dto.poe.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@SuperBuilder
@Data
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserLeaderboardResponseDto {

    @Schema(required = true, description = "User UUID from Keycloak")
    @NotNull
    private UUID keycloakUserId;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Image Url")
    @Size(max = 1024)
    private String imageUrl;
}
