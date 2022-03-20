package com.nft.platform.mapper;

import com.nft.platform.domain.Period;
import com.nft.platform.dto.response.PeriodResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeriodMapper {

    PeriodResponseDto toDto(Period period);
}
