package com.nft.platform.controller;

import com.nft.platform.common.dto.ContentAuthorDto;
import com.nft.platform.common.dto.UserProfileResponseDto;
import com.nft.platform.dto.request.EditUserProfileRequestDto;
import com.nft.platform.dto.request.KeycloakUserIdWithCelebrityIdDto;
import com.nft.platform.dto.request.ProfileWalletRequestDto;
import com.nft.platform.dto.request.UserProfileFilterDto;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.request.UserProfileSearchDto;
import com.nft.platform.dto.request.UserToCelebrityAttachmentRequestDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.dto.response.CurrentUserProfileWithWalletsResponseDto;
import com.nft.platform.dto.response.NftOwnerDto;
import com.nft.platform.dto.response.PoorUserProfileResponseDto;
import com.nft.platform.dto.response.UserProfileWithCelebrityIdsResponseDto;
import com.nft.platform.dto.response.UserProfileWithWalletsResponseDto;
import com.nft.platform.enums.OwnerType;
import com.nft.platform.service.UserProfileService;
import com.nft.platform.util.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.nft.platform.util.security.RoleConstants.*;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "User Profile Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final SecurityUtil securityUtil;

    @GetMapping("/search")
    @Operation(summary = "Search by user profile fields")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public ResponseEntity<List<UserProfileResponseDto>> usersSearch(UserProfileSearchDto searchParams) {
        return ResponseEntity.ok(userProfileService.search(searchParams));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User Profile by Id")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public ResponseEntity<UserProfileWithWalletsResponseDto> findUserById(@Parameter(name = "id", description = "User Profile Id")
                                                                          @PathVariable(name = "id") UUID userProfileId) {
        Optional<UserProfileWithWalletsResponseDto> userProfileResponseDtoO = userProfileService.findUserProfileById(userProfileId);
        return ResponseEntity.of(userProfileResponseDtoO);
    }

    @GetMapping("{id}/poor")
    @Operation(summary = "Get poor User Profile by Id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PoorUserProfileResponseDto> findOtherUserById(@Parameter(name = "id", description = "User Profile Id")
                                                                         @PathVariable(name = "id") UUID userProfileId) {
        Optional<PoorUserProfileResponseDto> poorUserProfileResponseDto = userProfileService.findPoorUserProfile(userProfileId);
        return ResponseEntity.of(poorUserProfileResponseDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER})
    public ResponseEntity<CurrentUserProfileWithWalletsResponseDto> findMeByKeycloakId(@RequestParam(required = false) UUID celebrityId) {
        Optional<CurrentUserProfileWithWalletsResponseDto> userProfileResponseDtoO = userProfileService.findCurrentUserProfile(celebrityId);
        return ResponseEntity.of(userProfileResponseDtoO);
    }

    @GetMapping("/me/celebrities/subscribed")
    @Operation(summary = "Get Current User subscriptions to celebrities")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER})
    public ResponseEntity<List<CelebrityResponseDto>> findMySubscriptions() {
        return ResponseEntity.of(Optional.of(userProfileService.findAllSubscribedCelebrities(getCurrentUserId())));
    }

    @GetMapping("/me/celebrities/unsubscribed")
    @Operation(summary = "Get Current User subscriptions to celebrities")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER})
    public Page<CelebrityResponseDto> findCelebritiesWithoutMySubscription(@PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return userProfileService.findAllUnsubscribedCelebrities(getCurrentUserId(), pageable);
    }

    private UUID getCurrentUserId() {
        return UUID.fromString(securityUtil.getCurrentUser().getId());
    }

    @GetMapping("/by-kk-id/{id}")
    @Operation(summary = "Get Current User Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public ResponseEntity<UserProfileWithCelebrityIdsResponseDto> findUserByKeycloakId(@Parameter(name = "id", description = "Keycloak User Id")
                                                                                       @PathVariable(name = "id") UUID keycloakId) {
        Optional<UserProfileWithCelebrityIdsResponseDto> dto = userProfileService.findUserProfileByKeycloakId(keycloakId);
        return ResponseEntity.of(dto);
    }

    @GetMapping("/base")
    @Operation(summary = "Get Page of User Profiles with base fields by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_TECH_TOKEN})
    public Page<UserProfileResponseDto> getUserBaseFieldsPage(
            @ParameterObject @Valid UserProfileFilterDto filterDto,
            @PageableDefault(sort = {"keycloakUserId"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return userProfileService.getUserProfileBaseFieldsPage(filterDto, pageable);
    }

    @GetMapping
    @Operation(summary = "Get Page of User Profiles by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM})
    public Page<UserProfileResponseDto> getUserPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return userProfileService.getUserProfilePage(pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM})
    public UserProfileResponseDto updateUserProfile(
            @Parameter(name = "id", description = "User Profile Id")
            @PathVariable("id") UUID userId,
            @Parameter(name = "userProfileRequestDto", description = "User Profile Request Dto")
            @Valid @RequestBody UserProfileRequestDto userProfileRequestDto
    ) {
        return userProfileService.updateUserProfile(userId, userProfileRequestDto);
    }

    @PatchMapping
    @Operation(summary = "Patch Fields in User Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER})
    public UserProfileResponseDto patchUserProfile(@Parameter(name = "userProfileRequestDto", description = "User Profile Request Dto")
                                                   @Valid @RequestBody EditUserProfileRequestDto editUserProfileRequestDto) {
        return userProfileService.patchUserProfile(editUserProfileRequestDto);
    }

    @PostMapping
    @Operation(summary = "Create/Update User Profile")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public UserProfileResponseDto createUpdateUserProfile(@Parameter(name = "userProfileRequestDto", description = "User Profile Request Dto")
                                                          @Valid @RequestBody UserProfileRequestDto userProfileRequestDto) {
        return userProfileService.createUpdateUserProfile(userProfileRequestDto);
    }

    @PutMapping("/add-profile-wallet")
    @Operation(summary = "Add Celebrity to User Profile")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM})
    public void addProfileWallet(@Valid @RequestBody ProfileWalletRequestDto celebrityForUserDto) {
        userProfileService.addProfileWallet(celebrityForUserDto);
    }

    @GetMapping("/is-connected-with-celebrity")
    @Operation(summary = "Check that User is connected with Celebrity")
    @ResponseStatus(HttpStatus.OK)
    public boolean isConnectedWithCelebrity(@Valid @ParameterObject KeycloakUserIdWithCelebrityIdDto requestDto) {
        return userProfileService.isConnectedWithCelebrity(requestDto);
    }

    @PostMapping(value = "/upload-user-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Profile Image")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER,
            ROLE_ADMIN_CELEBRITY, ROLE_CONTENT_MODERATOR, ROLE_ADMIN_PLATFORM,
            ROLE_TECH_TOKEN})
    public String uploadUserProfileImage(@RequestPart(name = "file") MultipartFile file) {
        return userProfileService.uploadUserProfileImageForCurrent(file);
    }

    @PutMapping("/remove-user-image")
    @Operation(summary = "Remove User Profile Image")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER,
            ROLE_ADMIN_CELEBRITY, ROLE_CONTENT_MODERATOR, ROLE_ADMIN_PLATFORM})
    public void removeUserProfileImage() {
        userProfileService.removeUserProfileImage();
    }

    @PostMapping(value = "/upload-user-banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Profile Banner")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER})
    public String uploadUserProfileBanner(@RequestPart(name = "file") MultipartFile file) {
        return userProfileService.uploadUserProfileBannerForCurrent(file);
    }

    @PutMapping("/remove-user-banner")
    @Operation(summary = "Remove User Profile Banner")
    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER})
    public void removeUserProfileBanner() {
        userProfileService.removeUserProfileBanner();
    }

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER,
            ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public ResponseEntity<List<UserProfileResponseDto>> getUsersInfo(@RequestParam(name = "userIds") List<UUID> userIds) {
        return ResponseEntity.of(ofNullable(userProfileService.getUsersInfo(userIds)));
    }
    @GetMapping("/avatars")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER,
            ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public ResponseEntity<Set<String>> getUsersAvatars(@RequestParam(name = "userIds") List<UUID> userIds) {
        return ResponseEntity.of(ofNullable(userProfileService.getUsersAvatars(userIds)));
    }

    @GetMapping("/info-by-kk-ids")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER,
            ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public ResponseEntity<List<UserProfileResponseDto>> getUsersInfoByKeycloakIds(
            @RequestParam(name = "keycloakUserIds") List<UUID> keycloakUserIds) {
        return ResponseEntity.of(ofNullable(userProfileService.getUsersInfoByKeycloakIds(keycloakUserIds)));
    }

    @GetMapping("/owner-infos/{userId}")
    @Secured(ROLE_TECH_TOKEN)
    public NftOwnerDto getNftOwnerInfo(@PathVariable UUID userId, @RequestParam OwnerType type, @RequestParam List<UUID> userIds) {
        return userProfileService.getOwnerInfo(userId, type, userIds);
    }

    @PostMapping("/attachments/users-to-celebrities")
    @Operation(summary = "Attach User to a celebrity")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User or celebrity was not found")
    })
    @Secured({ROLE_USER, ROLE_MARKETPLACE_USER,
            ROLE_ADMIN_CELEBRITY, ROLE_ADMIN_PLATFORM, ROLE_TECH_TOKEN})
    public ResponseEntity<?> attachUserToCelebrity(@RequestBody UserToCelebrityAttachmentRequestDto body) {
       userProfileService.attachCurrentUserToCelebrity(body.getCelebrityId());
       return ResponseEntity.ok().build();
    }

    @Hidden
    @GetMapping(value = "/authors/map", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get map of content authors by their Keycloak ids")
    @Secured(ROLE_TECH_TOKEN)
    @ResponseStatus(HttpStatus.OK)
    public Map<UUID, ContentAuthorDto> getContentAuthorsMap(@RequestParam Set<UUID> authorKeycloakIds) {
        return userProfileService.getContentAuthorsMapByKeycloakIdIn(authorKeycloakIds);
    }

    @GetMapping("/moderators/names/map")
    @Operation(summary = "Get moderator names map by their Keycloak ids")
    @Hidden
    @Secured(ROLE_TECH_TOKEN)
    @ResponseStatus(HttpStatus.OK)
    public Map<UUID, String> getModeratorNamesByKeycloakIds(@RequestParam Set<UUID> moderatorKeycloakIds) {
        return userProfileService.getModeratorNamesMapByKeycloakIds(moderatorKeycloakIds);
    }

    @GetMapping("/id-exchange")
    @Operation(summary = "Get user's Keycloak Id by User Id (DB primary key)")
    @Hidden
    @Secured(ROLE_TECH_TOKEN)
    @ResponseStatus(HttpStatus.OK)
    public UUID exchangeUserIdForKeycloakId(@RequestParam UUID userId) {
        return userProfileService.exchangeUserIdForKeycloakId(userId);
    }
}