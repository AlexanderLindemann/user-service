package com.nft.platform.dto.response;

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
@Schema(description = "User Response With Celebrity Ids DTO")
public class UserProfileWithCelebrityIdsResponseDto extends UserProfileResponseDto {

    @Schema(description = "Celebrity Ids")
    List<UUID> celebrityIds;
}
