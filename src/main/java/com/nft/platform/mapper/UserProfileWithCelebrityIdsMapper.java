package com.nft.platform.mapper;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.response.UserProfileWithCelebrityIdsResponseDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(UserProfileWithCelebrityIdsDecorator.class)
public interface UserProfileWithCelebrityIdsMapper {

    UserProfileWithCelebrityIdsResponseDto toDto(UserProfile userProfile);

}
