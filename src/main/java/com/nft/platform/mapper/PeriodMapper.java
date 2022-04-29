package com.nft.platform.mapper;

import com.nft.platform.common.dto.PeriodResponseDto;
import com.nft.platform.domain.Period;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeriodMapper {

    PeriodResponseDto toDto(Period period);
}
