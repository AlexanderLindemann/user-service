package com.nft.platform.mapper;

import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.poe.response.UserLeaderboardResponseDto;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.PoorUserProfileResponseDto;
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

    @Mapping(target = "keycloakUserId", source = "keycloakUserId")
    @Mapping(target = "imagePromoBannerUrl", source = "imagePromoBannerUrl")
    PoorUserProfileResponseDto toPoorDto(UserProfile userProfile);

    @Mappings({
            @Mapping(target = "roles", ignore = true)
    })
    UserProfileResponseDto toDtoWithBaseFields(UserProfile userProfile);

    @Mapping(source = "userProfile", target = "profileWalletDto", qualifiedByName = "setProfileWallet")
    UserProfileWithWalletResponseDto toDtoWithWallet(UserProfile userProfile,
                                                     @Context ProfileWallet profileWallet,
                                                     @Context Integer activityBalance);

    @Named("setProfileWallet")
    default ProfileWalletResponseDto setProfileWallet(UserProfile userProfile,
                                                      @Context ProfileWallet profileWallet,
                                                      @Context Integer activityBalance) {
        ProfileWalletResponseDto profileWalletResponseDto = toDto(profileWallet);
        profileWalletResponseDto.setActivityBalance(activityBalance);
        return profileWalletResponseDto;
    }

    ProfileWalletResponseDto toDto(ProfileWallet profileWallet);

    UserLeaderboardResponseDto toUserLeaderboardDto(UserProfile userProfile);
}
