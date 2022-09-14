package com.nft.platform.dto.response;

import com.nft.platform.common.enums.CelebrityThemeType;
import com.nft.platform.dto.AbstractCelebrityDto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Celebrity Response DTO")
public class CelebrityResponseDto extends AbstractCelebrityDto {

    @Schema(description = "Celebrity id", required = true)
    private UUID id;

    @Schema(description = "Celebrity categories", required = true)
    private List<CelebrityCategoryResponseDto> category;

    @Schema(description = "User is subscriber", required = true)
    private boolean isSubscribed = false;

    @Schema(description = "Celebrity type")
    private CelebrityThemeType themeType;

}