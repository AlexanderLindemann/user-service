package com.nft.platform.controller;

import com.nft.platform.dto.request.KeycloakUserIdWithCelebrityIdDto;
import com.nft.platform.dto.request.ProfileWalletRequestDto;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.UserProfileResponseDto;
import com.nft.platform.service.UserProfileService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "User Profile Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    @Operation(summary = "Get User Profile by Id")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM})
    public ResponseEntity<UserProfileResponseDto> findUserById(
            @Parameter(name = "id", description = "User Profile Id")
            @PathVariable(name = "id") UUID userProfileId
    ) {
        Optional<UserProfileResponseDto> userProfileResponseDtoO = userProfileService.findUserProfileById(userProfileId);
        return ResponseEntity.of(userProfileResponseDtoO);
    }

    @GetMapping
    @Operation(summary = "Get Page of User Profiles by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM})
    public Page<UserProfileResponseDto> getUserPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return userProfileService.getUserProfilePage(pageable);
    }

    @GetMapping("/votes")
    @Operation(summary = "Find User Vote Balance by Celebrity")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public ResponseEntity<Integer> findUserVotes(
            @RequestParam("keycloakUserId") UUID keycloakUserId,
            @RequestParam("celebrityId") UUID celebrityId
    ) {
        Optional<Integer> userVotesO = userProfileService.findUserVotes(keycloakUserId, celebrityId);
        return ResponseEntity.of(userVotesO);
    }

    @PutMapping("/decrement-votes")
    @Operation(summary = "Decrement User Votes")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public int decrementUserVotes(
            @Valid @RequestBody KeycloakUserIdWithCelebrityIdDto requestDto
    ) {
        return userProfileService.decrementUserVotes(requestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM})
    public UserProfileResponseDto updateUserProfile(
            @Parameter(name = "id", description = "User Profile Id")
            @PathVariable("id") UUID userId,
            @Parameter(name = "userProfileRequestDto", description = "User Profile Request Dto")
            @Valid @RequestBody UserProfileRequestDto userProfileRequestDto
    ) {
        return userProfileService.updateUserProfile(userId, userProfileRequestDto);
    }

    @PostMapping
    @Operation(summary = "Create User Profile")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public UserProfileResponseDto createUserProfile(
            @Parameter(name = "userProfileRequestDto", description = "User Profile Request Dto")
            @Valid @RequestBody UserProfileRequestDto userProfileRequestDto
    ) {
        return userProfileService.createUserProfile(userProfileRequestDto);
    }

    @PutMapping("/add-profile-wallet")
    @Operation(summary = "Add Celebrity to User Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM})
    public void addProfileWallet(@Valid @RequestBody ProfileWalletRequestDto celebrityForUserDto) {
        userProfileService.addProfileWallet(celebrityForUserDto);
    }
}
