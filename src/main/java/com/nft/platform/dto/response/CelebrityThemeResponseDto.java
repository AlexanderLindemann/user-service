package com.nft.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@SuperBuilder
@Data
@NoArgsConstructor
@Schema(description = "Celebrity Theme Response DTO")
public class CelebrityThemeResponseDto {
    @Schema(description = "Celebrity Id", required = true)
    @NotNull
    private UUID celebrityId;

    @Schema(description = "Celebrity Theme", required = true)
    @NotNull
    private String theme;
}
