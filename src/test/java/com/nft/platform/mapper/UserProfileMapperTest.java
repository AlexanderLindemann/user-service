package com.nft.platform.mapper;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.UserProfileResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        UserProfileMapperImpl.class
})
public class UserProfileMapperTest {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Test
    @DisplayName("Update UserProfile from request Dto")
    void updateUserProfileFromRequestDto() {
        UserProfile userProfile = createDummyUserProfile();
        UserProfileRequestDto userProfileRequestDto = createDummyUserProfileRequestDto();
        userProfile = userProfileMapper.toEntity(userProfileRequestDto, userProfile);
        assertEquals(userProfileRequestDto.getImageUrl(), userProfile.getImageUrl());
        assertEquals(userProfileRequestDto.getPhone(), userProfile.getPhone());
        assertEquals(userProfileRequestDto.getVerifiedPhone(), userProfile.isVerifiedPhone());
        assertEquals(userProfileRequestDto.getInvisibleName(), userProfile.isInvisibleName());
        assertEquals(userProfileRequestDto.getTwoFactoAuth(), userProfile.isTwoFactoAuth());
        assertEquals(userProfileRequestDto.getGoogleId(), userProfile.getGoogleId());
        assertEquals(userProfileRequestDto.getFacebookId(), userProfile.getFacebookId());
        assertEquals(userProfileRequestDto.getTwitterId(), userProfile.getTwitterId());
        assertEquals(userProfileRequestDto.getDescription(), userProfile.getDescription());
        assertEquals(userProfileRequestDto.getSite(), userProfile.getSite());
    }

    @Test
    @DisplayName("Convert UserProfile to Response Dto")
    void mapUserProfileToResponseDto() {
        UserProfile userProfile = createDummyUserProfile();
        UserProfileResponseDto dto = userProfileMapper.toDto(userProfile);
        assertEquals(userProfile.getImageUrl(), dto.getImageUrl());
        assertEquals(userProfile.getPhone(), dto.getPhone());
        assertEquals(userProfile.isVerifiedPhone(), dto.getVerifiedPhone());
        assertEquals(userProfile.isInvisibleName(), dto.getInvisibleName());
        assertEquals(userProfile.isTwoFactoAuth(), dto.getTwoFactoAuth());
        assertEquals(userProfile.getGoogleId(), dto.getGoogleId());
        assertEquals(userProfile.getFacebookId(), dto.getFacebookId());
        assertEquals(userProfile.getTwitterId(), dto.getTwitterId());
        assertEquals(userProfile.getDescription(), dto.getDescription());
        assertEquals(userProfile.getSite(), dto.getSite());
    }

    private UserProfile createDummyUserProfile() {
        return UserProfile.builder()
                .imageUrl("image")
                .phone("1234567890123")
                .verifiedPhone(true)
                .invisibleName(true)
                .twoFactoAuth(false)
                .googleId("googleId")
                .facebookId("facebookId")
                .twitterId("twitterId")
                .description("desc")
                .site("site")
                .build();
    }

    private UserProfileRequestDto createDummyUserProfileRequestDto() {
        return UserProfileRequestDto.builder()
                .imageUrl("image-up")
                .phone("1234567890123-up")
                .verifiedPhone(false)
                .invisibleName(false)
                .twoFactoAuth(true)
                .googleId("googleId-up")
                .facebookId("facebookId-up")
                .twitterId("twitterId-up")
                .description("desc-up")
                .site("site-up")
                .build();
    }

}
