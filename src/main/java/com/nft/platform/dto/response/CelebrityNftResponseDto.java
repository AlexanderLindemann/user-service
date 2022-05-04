package com.nft.platform.dto.response;

import com.nft.platform.dto.AbstractCelebrityDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Celebrity Response DTO with nft Count")
public class CelebrityNftResponseDto extends AbstractCelebrityDto {
    @Schema(description = "Celebrity id", required = true)
    private UUID id;

    @Schema(description = "Celebrity nft count", required = true)
    private Integer nftCount;
}
