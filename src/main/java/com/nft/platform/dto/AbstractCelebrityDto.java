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

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractCelebrityDto {

    @Schema(description = "Celebrity name", required = true)
    @Size(max = 1024)
    @NotNull
    private String name;

    @Schema(description = "Celebrity nickname")
    @Size(max = 1024)
    private String nickName;

    @Schema(description = "Celebrity avatar image direct url")
    @Size(max = 1024)
    private String imageUrl;

    @Schema(description = "Celebrity signature image url")
    @Size(max = 1024)
    private String celebritySignature;

    @Schema(description = "Celebrity video url")
    @Size(max = 1024)
    private String celebrityVideo;

    @Schema(description = "Celebrity description", required = true)
    private String description;

    @Schema(description = "Celebrity android application link", required = true)
    private String androidLink;

    @Schema(description = "Celebrity ios application link", required = true)
    private String iosLink;

    @Schema(description = "Celebrity banner image direct url", required = true)
    private String imagePromoUrl;

    @Schema(description = "Celebrity theme", required = true)
    private Object jsonTheme;

    @Schema(description = "Celebrity last name")
    private String lastName;

    @Schema(description = "Celebrity image card mp")
    private String imageCardMp;

    @Schema(description = "Celebrity image card app")
    private String imageCardApp;

    @Schema(description = "Celebrity image promo app")
    private String imagePromoApp;

    @Schema(description = "Celebrity image login")
    private String imageLogin;

    @Schema(description = "Celebrity image nft owner")
    private String imageNftOwner;

    @Schema(description = "Celebrity image screen")
    private String imageScreen;

    @Schema(description = "Celebrity active status")
    private boolean active;

    @Schema(description = "Celebrity application screen.")
    private String appScreen;

    @Schema(description = "Celebrity header avatar")
    private String headerAvatar;

}
