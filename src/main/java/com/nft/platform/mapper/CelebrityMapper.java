package com.nft.platform.mapper;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CelebrityMapper {

    Celebrity toEntity(CelebrityRequestDto requestDto, @MappingTarget Celebrity celebrity);

    CelebrityResponseDto toDto(Celebrity celebrity);
}
