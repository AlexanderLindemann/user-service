package com.nft.platform.dto.request;

import com.nft.platform.common.dto.MinimizedAbstractUserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Edit User Request DTO")
public class EditUserProfileRequestDto extends MinimizedAbstractUserProfileDto {
}
