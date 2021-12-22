package com.nft.platform.util;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@ActiveProfiles("test")
public class UserProfileGenerator {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Transactional
    public UserProfile createAndSaveDummyUserProfile() {
        UserProfile userProfile = createDummyUserProfile();
        return userProfileRepository.save(userProfile);
    }

    public UserProfile createDummyUserProfile() {
        return UserProfile.builder()
                .keycloakUserId(UUID.randomUUID())
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
                .createdBy("test-createdBy")
                .build();
    }
}
