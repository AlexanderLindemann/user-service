package com.nft.platform.mapper;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.UserProfileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {

    UserProfile toEntity(UserProfileRequestDto requestDto, @MappingTarget UserProfile userProfile);

    UserProfileResponseDto toDto(UserProfile userProfile);

    @Mappings({
            @Mapping(target = "roles", ignore = true)
    })
    UserProfileResponseDto toDtoWithBaseFields(UserProfile userProfile);
}
