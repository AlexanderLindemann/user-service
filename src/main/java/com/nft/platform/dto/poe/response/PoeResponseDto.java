package com.nft.platform.dto.poe.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nft.platform.dto.poe.AbstractPoeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;


@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class PoeResponseDto extends AbstractPoeDto {

    private UUID id;

}
