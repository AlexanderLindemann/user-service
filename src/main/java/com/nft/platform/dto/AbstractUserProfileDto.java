package com.nft.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractUserProfileDto extends MinimizedAbstractUserProfileDto {

    @Schema(required = true, description = "User UUID from Keycloak")
    @NotNull
    private UUID keycloakUserId;

    // updated from keycloak
    @Schema(description = "Username")
    private String username;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Phone")
    @Size(max = 13)
    private String phone;

    @Schema(description = "Is Phone Verified")
    private Boolean verifiedPhone;

    @Schema(description = "Is Two-Factor Authentication")
    private Boolean twoFactoAuth;

    @Schema(description = "Google Id")
    @Size(max = 128)
    private String googleId;

    @Schema(description = "Image Url")
    @Size(max = 1024)
    private String imageUrl;

    @Schema(description = "Image Promo Banner Url")
    @Size(max = 1024)
    private String imagePromoBannerUrl;

    @Schema(description = "Balance of All Celebrity")
    private Long pointBalanceAll;

}
