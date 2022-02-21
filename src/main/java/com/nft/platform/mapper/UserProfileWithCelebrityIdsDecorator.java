package com.nft.platform.mapper;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.response.UserProfileWithCelebrityIdsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class UserProfileWithCelebrityIdsDecorator implements UserProfileWithCelebrityIdsMapper {

    @Autowired
    @Qualifier("delegate")
    private UserProfileWithCelebrityIdsMapper delegate;

    @Override
    public UserProfileWithCelebrityIdsResponseDto toDto(UserProfile userProfile) {
        UserProfileWithCelebrityIdsResponseDto dto = delegate.toDto(userProfile);
        List<UUID> ids = userProfile.getProfileWallets().stream().map(pw -> pw.getCelebrity().getId()).collect(Collectors.toList());
        dto.setCelebrityIds(ids);
        return dto;
    }
}
