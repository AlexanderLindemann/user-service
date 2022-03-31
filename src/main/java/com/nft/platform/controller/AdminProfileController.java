package com.nft.platform.controller;

import com.nft.platform.dto.response.UserProfileWithCelebrityIdsResponseDto;
import com.nft.platform.service.UserProfileService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "Admin Profile Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @Operation(summary = "Get Current Admin Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_CONTENT_MODERATOR})
    public ResponseEntity<UserProfileWithCelebrityIdsResponseDto> findMeAdminByKeycloakId() {
        Optional<UserProfileWithCelebrityIdsResponseDto> adminProfileResponseDto = userProfileService.findCurrentAdminProfile();
        return ResponseEntity.of(adminProfileResponseDto);
    }
}
