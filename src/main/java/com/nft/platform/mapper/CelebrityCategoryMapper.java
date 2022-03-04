package com.nft.platform.mapper;

import com.nft.platform.domain.CelebrityCategory;
import com.nft.platform.dto.request.CelebrityCategoryRequestDto;
import com.nft.platform.dto.response.CelebrityCategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CelebrityCategoryMapper {

    CelebrityCategory toEntity(CelebrityCategoryRequestDto requestDto, @MappingTarget CelebrityCategory celebrityCategory);

    CelebrityCategoryResponseDto toDto(CelebrityCategory celebrityCategory);
}
