package com.nft.platform.service;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.UserProfileResponseDto;
import com.nft.platform.util.AbstractIntegrationTest;
import com.nft.platform.util.UserProfileGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class UserProfileServiceTest extends AbstractIntegrationTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileGenerator userProfileGenerator;

    @Test
    @DisplayName("Get UserProfile by Id")
    void getUserProfileById() {
        UserProfile userProfile = userProfileGenerator.createAndSaveDummyUserProfile();
        Optional<UserProfileResponseDto> dto = userProfileService.findUserProfileById(userProfile.getId());
        assertTrue(dto.isPresent());
    }

    @Test
    @DisplayName("Get UserProfile Page")
    void getUserProfilePage() {
        userProfileGenerator.createAndSaveDummyUserProfile();
        PageRequest page = PageRequest.of(0, 1);
        Page<UserProfileResponseDto> dto = userProfileService.getUserProfilePage(page);
        assertEquals(1, dto.getContent().size());
    }

    @Test
    @DisplayName("Update UserProfile")
    void updateUserProfile() {
        UserProfile userProfile = userProfileGenerator.createAndSaveDummyUserProfile();
        UserProfileRequestDto requestDto = createDummyRequestDto();
        UserProfileResponseDto dto = userProfileService.updateUserProfile(userProfile.getId(), requestDto);
        assertEquals(requestDto.getImageUrl(), dto.getImageUrl());
    }

    @Test
    @DisplayName("Create UserProfile")
    void createUserProfile() {
        UserProfileRequestDto requestDto = createDummyRequestDto();
        UserProfileResponseDto dto = userProfileService.createUserProfile(requestDto);
        assertNotNull(dto.getId());
    }

    private UserProfileRequestDto createDummyRequestDto() {
        return UserProfileRequestDto.builder()
                .imageUrl("updated-image")
                .keycloakUserId(UUID.randomUUID())
                .build();
    }
}
