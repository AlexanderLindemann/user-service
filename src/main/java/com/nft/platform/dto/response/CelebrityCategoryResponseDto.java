package com.nft.platform.dto.response;

import com.nft.platform.dto.AbstractCelebrityCategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Celebrity Category Response DTO")
public class CelebrityCategoryResponseDto extends AbstractCelebrityCategoryDto {

    @Schema(description = "Celebrity Category Id", required = true)
    private UUID id;
}
