package com.nft.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractUserProfileDto {

    @Schema(required = true, description = "User UUID from Keycloak")
    @NotNull
    private UUID keycloakUserId;

    @Schema(description = "Image Url")
    @Size(max = 1024)
    private String imageUrl;

    @Schema(description = "Phone")
    @Size(max = 13)
    private String phone;

    @Schema(description = "Is Phone Verified")
    private boolean verifiedPhone;

    @Schema(description = "Is Invisible Name")
    private boolean invisibleName;

    @Schema(description = "Is Two-Factor Authentication")
    private boolean twoFactoAuth;

    @Schema(name = "Google Id")
    @Size(max = 128)
    private String googleId;

    @Schema(name = "Facebook Id")
    @Size(max = 128)
    private String facebookId;

    @Schema(name = "Twitter Id")
    @Size(max = 128)
    private String twitterId;

    @Schema(name = "Description")
    @Size(max = 1024)
    private String description;

    @Schema(name = "Site")
    @Size(max = 1024)
    private String site;

    public void setVerifiedPhone(Boolean verifiedPhone) {
        this.verifiedPhone = Boolean.TRUE.equals(verifiedPhone);
    }

    public void setInvisibleName(Boolean invisibleName) {
        this.invisibleName = Boolean.TRUE.equals(invisibleName);
    }

    public void setTwoFactoAuth(Boolean twoFactoAuth) {
        this.twoFactoAuth = Boolean.TRUE.equals(twoFactoAuth);
    }
}
