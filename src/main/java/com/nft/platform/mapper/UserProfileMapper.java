package com.nft.platform.mapper;

import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.ProfileWalletResponseDto;
import com.nft.platform.dto.response.UserProfileResponseDto;
import com.nft.platform.dto.response.UserProfileWithWalletResponseDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
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

    @Mapping(source = "userProfile", target = "profileWalletDto", qualifiedByName = "setProfileWallet")
    UserProfileWithWalletResponseDto toDtoWithWallet(UserProfile userProfile, @Context ProfileWallet profileWallet);

    @Named("setProfileWallet")
    default ProfileWalletResponseDto setProfileWallet(UserProfile userProfile, @Context ProfileWallet profileWallet) {
        return toDto(profileWallet);
    }

    ProfileWalletResponseDto toDto(ProfileWallet profileWallet);
}
