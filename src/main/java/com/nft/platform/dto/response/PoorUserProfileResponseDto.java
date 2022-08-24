package com.nft.platform.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public class PoorUserProfileResponseDto {

    private String firstName;

    private String lastName;

    private String nickname;

    private Boolean invisibleName;

    private String description;

    private String site;

    private String facebookId;

    private String twitterId;

    private String googleId;

    private String imageUrl;

    private String imagePromoBannerUrl;

    private LocalDateTime createdAt;

}
