package com.nft.platform.dto.request;

import com.nft.platform.dto.AbstractUserProfileDto;
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
@Schema(description = "User Request DTO")
public class UserProfileRequestDto extends AbstractUserProfileDto {

    @Schema(description = "Celebrity Id")
    private UUID celebrityId;

}
