package com.nft.platform.dto.response;

import com.nft.platform.dto.MinimizedAbstractUserProfileDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PoorUserProfileResponseDto extends MinimizedAbstractUserProfileDto {

    private String googleId;

    private String imageUrl;

    private String imagePromoBannerUrl;

    private UUID keycloakUserId;

    private LocalDateTime createdAt;

}
