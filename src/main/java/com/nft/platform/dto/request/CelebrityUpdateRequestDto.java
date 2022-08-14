package com.nft.platform.dto.request;

import com.nft.platform.dto.AbstractCelebrityDto;
import io.swagger.v3.oas.annotations.Hidden;
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
@Schema(description = "Celebrity Request DTO")
public class CelebrityUpdateRequestDto extends AbstractCelebrityDto {

    @Schema(description = "Celebrity categories", required = true)
    private List<UUID> category;
    @Hidden
    private Object jsonTheme;
}
