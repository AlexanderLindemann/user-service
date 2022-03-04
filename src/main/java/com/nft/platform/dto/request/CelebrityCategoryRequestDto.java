package com.nft.platform.dto.request;

import com.nft.platform.dto.AbstractCelebrityCategoryDto;
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
@Schema(description = "Celebrity Category Request DTO")
public class CelebrityCategoryRequestDto extends AbstractCelebrityCategoryDto {
}
