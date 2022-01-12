package com.nft.platform.dto.response;

import com.nft.platform.dto.AbstractCelebrityDto;
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
@Schema(description = "Celebrity Response DTO")
public class CelebrityResponseDto extends AbstractCelebrityDto {

    @Schema(description = "Celebrity Id", required = true)
    private UUID id;
}
