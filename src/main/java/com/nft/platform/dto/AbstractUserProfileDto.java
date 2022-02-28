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
public abstract class AbstractUserProfileDto {

    @Schema(required = true, description = "User UUID from Keycloak")
    @NotNull
    private UUID keycloakUserId;

    // updated from keycloak
    @Schema(description = "Username")
    private String username;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Image Url")
    @Size(max = 1024)
    private String imageUrl;

    @Schema(description = "Phone")
    @Size(max = 13)
    private String phone;

    @Schema(description = "Is Phone Verified")
    private Boolean verifiedPhone;

    @Schema(description = "Is Invisible Name")
    private Boolean invisibleName;

    @Schema(description = "Is Two-Factor Authentication")
    private Boolean twoFactoAuth;

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

    @Schema(description = "First Name")
    @Size(max = 128)
    private String firstName;

    @Schema(description = "Last Name")
    @Size(max = 128)
    private String lastName;

    @Schema(description = "Nickname")
    @Size(max = 128)
    private String nickname;

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
