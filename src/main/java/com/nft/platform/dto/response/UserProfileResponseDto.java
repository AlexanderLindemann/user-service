package com.nft.platform.dto.response;

import com.nft.platform.dto.AbstractUserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "User Response DTO")
public class UserProfileResponseDto extends AbstractUserProfileDto {

    @Schema(description = "User Id")
    private UUID id;

    @Schema(description = "User Registration Date")
    private LocalDateTime createdAt;
}
