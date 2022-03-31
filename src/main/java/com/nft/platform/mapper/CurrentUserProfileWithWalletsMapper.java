package com.nft.platform.mapper;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.response.CurrentUserProfileWithWalletsResponseDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(CurrentUserProfileWithWalletsDecorator.class)
public interface CurrentUserProfileWithWalletsMapper {

    CurrentUserProfileWithWalletsResponseDto toDto(UserProfile userProfile);

}
