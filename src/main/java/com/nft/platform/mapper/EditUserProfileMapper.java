package com.nft.platform.mapper;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.EditUserProfileRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EditUserProfileMapper {

    UserProfile toEntity(EditUserProfileRequestDto requestDto, @MappingTarget UserProfile userProfile);
}
