package com.nft.platform.dto.poe.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nft.platform.common.dto.AbstractPoeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PoeRequestDto extends AbstractPoeDto {

}
