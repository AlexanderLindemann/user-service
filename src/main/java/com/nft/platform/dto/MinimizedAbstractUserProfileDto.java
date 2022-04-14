package com.nft.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class MinimizedAbstractUserProfileDto {

    @Schema(description = "First Name")
    @Size(max = 128)
    private String firstName;

    @Schema(description = "Last Name")
    @Size(max = 128)
    private String lastName;

    @Schema(description = "Nickname")
    @Size(max = 128)
    private String nickname;

    @Schema(description = "Is Invisible Name")
    private Boolean invisibleName;

    @Schema(description = "Description")
    @Size(max = 1024)
    private String description;

    @Schema(description = "Is Invisible Email")
    private Boolean invisibleEmail;

    @Schema(description = "Is Invisible phone")
    private Boolean invisiblePhone;

    @Schema(description = "Site")
    @Size(max = 1024)
    private String site;

    @Schema(description = "Facebook Id")
    @Size(max = 128)
    private String facebookId;

    @Schema(description = "Twitter Id")
    @Size(max = 128)
    private String twitterId;

}
