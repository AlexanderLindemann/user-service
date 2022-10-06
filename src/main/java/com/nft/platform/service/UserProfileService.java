package com.nft.platform.service;

import com.nft.platform.common.dto.ContentAuthorDto;
import com.nft.platform.common.dto.UserProfileResponseDto;
import com.nft.platform.common.enums.FileType;
import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.domain.view.ModeratorView;
import com.nft.platform.dto.request.EditUserProfileRequestDto;
import com.nft.platform.dto.request.KeycloakUserIdWithCelebrityIdDto;
import com.nft.platform.dto.request.ProfileWalletRequestDto;
import com.nft.platform.dto.request.UserProfileFilterDto;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.request.UserProfileSearchDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.dto.response.CurrentUserProfileWithWalletsResponseDto;
import com.nft.platform.dto.response.NftOwnerDto;
import com.nft.platform.dto.response.PoorUserProfileResponseDto;
import com.nft.platform.dto.response.UserProfileWithCelebrityIdsResponseDto;
import com.nft.platform.dto.response.UserProfileWithWalletsResponseDto;
import com.nft.platform.enums.OwnerType;
import com.nft.platform.exception.FileUploadException;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.feign.client.FileServiceClient;
import com.nft.platform.mapper.Base64ToMultipartFileMapper;
import com.nft.platform.mapper.CelebrityMapper;
import com.nft.platform.mapper.CurrentUserProfileWithWalletsMapper;
import com.nft.platform.mapper.EditUserProfileMapper;
import com.nft.platform.mapper.UserProfileMapper;
import com.nft.platform.mapper.UserProfileWithCelebrityIdsMapper;
import com.nft.platform.mapper.UserProfileWithWalletsMapper;
import com.nft.platform.repository.CelebrityRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.repository.spec.UserProfileSpecifications;
import com.nft.platform.util.security.SecurityUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nft.platform.consts.Consts.TECH_CELEBRITY_ID;
import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    @Value("${nft.celebrity.default-uuid}")
    private String defaultCelebrity;

    @Value("${uploading.file.min-allowed-file-size}")
    private String MINIMAL_ALLOWED_FILE_SIZE;

    private final UserProfileRepository userProfileRepository;
    private final CelebrityRepository celebrityRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final UserProfileMapper mapper;
    private final EditUserProfileMapper editUserProfileMapper;
    private final UserProfileWithCelebrityIdsMapper mapperWithCelebrityIds;
    private final UserProfileWithWalletsMapper userProfileWithWalletsMapper;
    private final CurrentUserProfileWithWalletsMapper currentUserProfileWithWalletsMapper;
    private final SecurityUtil securityUtil;
    private final ProfileWalletService profileWalletService;
    private final FileServiceClient fileServiceClient;
    private final CelebrityMapper celebrityMapper;

    @NonNull
    @Transactional(readOnly = true)
    public Optional<UserProfileWithWalletsResponseDto> findUserProfileById(@NonNull UUID id) {
        return userProfileRepository.findByIdWithWallets(id)
                .map(userProfileWithWalletsMapper::toDto);
    }

    @NonNull
    public Optional<PoorUserProfileResponseDto> findPoorUserProfile(@NonNull UUID id) {
        return userProfileRepository.findById(id)
                .map(mapper::toPoorDto);
    }

    public Optional<UserProfile> findByLogin(@NonNull String login) {
        return userProfileRepository.findUserProfileBy(StringUtils.toRootLowerCase(login), StringUtils.toRootLowerCase(login),StringUtils.toRootLowerCase(login))
                .stream()
                .findFirst();
    }

    public List<UserProfileResponseDto> search(UserProfileSearchDto params) {
        return userProfileRepository.findUserProfileBy(StringUtils.toRootLowerCase(params.getName()), StringUtils.toRootLowerCase(params.getEmail()), params.getPhone())
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @NonNull
    @Transactional(readOnly = true)
    public Page<UserProfileResponseDto> getUserProfilePage(@NonNull Pageable pageable) {
        Page<UserProfile> userProfilePage = userProfileRepository.findAll(pageable);
        return userProfilePage.map(mapper::toDto);
    }

    @NonNull
    @Transactional(readOnly = true)
    public Page<UserProfileResponseDto> getUserProfileBaseFieldsPage(
            UserProfileFilterDto filterDto,
            @NonNull Pageable pageable
    ) {
        Specification<UserProfile> spec = UserProfileSpecifications.fromUserProfileFilter(filterDto);
        Page<UserProfile> userProfilePage = userProfileRepository.findAll(spec, pageable);
        return userProfilePage.map(mapper::toDtoWithBaseFields);
    }

    @NonNull
    @Transactional
    public UserProfileResponseDto updateUserProfile(@NonNull UUID id, @NonNull UserProfileRequestDto requestDto) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, id));
        userProfile = mapper.toEntity(requestDto, userProfile);
        userProfileRepository.save(userProfile);
        return mapper.toDto(userProfile);
    }


    @NonNull
    @Transactional
    public UserProfileResponseDto patchUserProfile(@NonNull EditUserProfileRequestDto editRequestDto) {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());
        UserProfile userProfile = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));

        userProfile = editUserProfileMapper.toEntity(editRequestDto, userProfile);
        userProfile = userProfileRepository.save(userProfile);
        return mapper.toDto(userProfile);
    }

    @NonNull
    @Transactional
    public UserProfileResponseDto createUpdateUserProfile(@NonNull UserProfileRequestDto requestDto) {
        log.info("createUpdateUserProfile request = {}", requestDto);

        UserProfile userProfile;
        Optional<UserProfile> existed = userProfileRepository.findByKeycloakUserId(requestDto.getKeycloakUserId());

        if (existed.isPresent()) {
            userProfile = mapper.toEntity(requestDto, existed.get());
            log.info("updating existed UserProfile with id = {}", userProfile.getId());
        } else {
            log.info("creating new UserProfile from request = {}", requestDto);
            userProfile = new UserProfile();
            userProfile = mapper.toEntity(requestDto, userProfile);
        }

        userProfile = userProfileRepository.save(userProfile);

        if (requestDto.getCelebrityId() != null) {
            Celebrity celebrity = celebrityRepository.findById(requestDto.getCelebrityId())
                    .orElseGet(() -> {
                        log.error("Celebrity with ID = {} not found!", requestDto.getCelebrityId());
                        return null;
                    });

            if (celebrity != null) {
                Optional<ProfileWallet> existedProfileWallet = profileWalletRepository.findByUserProfileIdAndCelebrityId(userProfile.getId(), requestDto.getCelebrityId());
                // add new profileWallet if not exists
                if (existedProfileWallet.isEmpty()) {
                    log.info("adding new connection with CELEBRITY_ID = {} for USER = {} with userProfileId = {}", requestDto.getCelebrityId(), userProfile.getUsername(), userProfile.getId());
                    profileWalletService.createAndSaveProfileWallet(userProfile, celebrity);
                } else {
                    log.info("ProfileWallet for CELEBRITY_ID = {} and USER = {} with userProfileId = {} existed", requestDto.getCelebrityId(), userProfile.getUsername(), userProfile.getId());
                }
            }
        }

        return mapper.toDto(userProfile);
    }

    @Transactional
    public void addProfileWallet(@NonNull ProfileWalletRequestDto profileWalletRequestDto) {
        Optional<ProfileWallet> profileWalletO = profileWalletRepository.findByUserProfileIdAndCelebrityId(
                profileWalletRequestDto.getUserProfileId(),
                profileWalletRequestDto.getCelebrityId()
        );
        if (profileWalletO.isPresent()) {
            throw new ItemConflictException(ProfileWallet.class, profileWalletO.get().getId());
        }
        UserProfile userProfile = userProfileRepository.findById(profileWalletRequestDto.getUserProfileId())
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, profileWalletRequestDto.getUserProfileId()));
        Celebrity celebrity = celebrityRepository.findById(profileWalletRequestDto.getCelebrityId())
                .orElseThrow(() -> new ItemNotFoundException(Celebrity.class, profileWalletRequestDto.getCelebrityId()));

        profileWalletService.createAndSaveProfileWallet(userProfile, celebrity);
    }

    @Transactional
    public Optional<CurrentUserProfileWithWalletsResponseDto> findCurrentUserProfile(@Nullable UUID activeCelebrityId) {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());
        // For Mobile Users celebrityId gets from clientId
        Optional<UserProfile> userProfileO;
        if (isNull(activeCelebrityId)) {
            userProfileO = userProfileRepository.findByKeycloakIdWithCryptoWallets(keycloakUserId);
        } else {
            userProfileO = userProfileRepository.findByKeycloakIdAndCelebrityIdWithWallets(keycloakUserId, activeCelebrityId);
        }
        if (userProfileO.isEmpty()) {
            attachUserToCelebrity(keycloakUserId, TECH_CELEBRITY_ID);
        }

        var userProfile = userProfileRepository.findByKeycloakUserId(keycloakUserId);
        userProfile.ifPresent(profile -> {
            if (profile.getProfileWallets().isEmpty()) {
                profile.setProfileWallets(Set.of(attachUserToCelebrity(keycloakUserId, TECH_CELEBRITY_ID)));
            }
        });

        return userProfile.stream()
                .map(currentUserProfileWithWalletsMapper::toDto)
                .peek((e) -> e.setRoles(currentUser.getRoles()))
                .findAny();
    }

    @Transactional(readOnly = true)
    public List<CelebrityResponseDto> findAllSubscribedCelebrities(UUID keycloakUserId) {
        return celebrityRepository.findAllSubscribedCelebrities(keycloakUserId, TECH_CELEBRITY_ID)
                .stream()
                .map(celebrityMapper::toDto)
                .peek(celebrity -> celebrity.setSubscribed(true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CelebrityResponseDto> findAllUnsubscribedCelebrities(UUID keycloakUserId, Pageable pageable) {
        return celebrityRepository.findAllUnsubscribedCelebrities(keycloakUserId, pageable, TECH_CELEBRITY_ID)
                .map(celebrityMapper::toDto)
                .map(celebrity -> {
                    celebrity.setSubscribed(false);
                    return celebrity;
                });
    }

    @Transactional
    public Optional<UserProfileWithCelebrityIdsResponseDto> findCurrentAdminProfile() {
        UUID keycloakUserId = UUID.fromString(securityUtil.getCurrentUser().getId());
        return findUserProfileByKeycloakId(keycloakUserId);
    }

    /**
     * Returns an user's profile and creates subscription to {@link com.nft.platform.consts.Consts#TECH_CELEBRITY_ID} if
     * the user doesn't have subscriptions at all.
     *
     * @param keycloakId keycloakUserId from auth token
     * @return instance of {@link UserProfileWithCelebrityIdsResponseDto}
     */
    @Transactional(readOnly = true)
    public Optional<UserProfileWithCelebrityIdsResponseDto> findUserProfileByKeycloakId(UUID keycloakId) {
        return userProfileRepository.findByKeycloakUserIdWithCelebrities(keycloakId)
                .map(profile -> {
                    if (profile.getProfileWallets().isEmpty()) {
                        profile.setProfileWallets(Set.of(attachUserToCelebrity(keycloakId, TECH_CELEBRITY_ID)));
                    }
                    return profile;
                })
                .map(mapperWithCelebrityIds::toDto);
    }

    @Transactional(readOnly = true)
    public boolean isConnectedWithCelebrity(KeycloakUserIdWithCelebrityIdDto requestDto) {
        return userProfileRepository.findByKeycloakUserIdAndCelebrityId(
                requestDto.getKeycloakUserId(), requestDto.getCelebrityId()).isPresent();
    }

    @Transactional
    public String uploadUserProfileImageForUserById(@NotNull UUID id, @NotNull MultipartFile file) {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, id));

        String oldUrl = profile.getImageUrl();
        // trying upload new image
        String url = replaceUserProfileImage(file, oldUrl, FileType.AVATAR);
        profile.setImageUrl(url);
        userProfileRepository.save(profile);
        return url;
    }


    @Transactional
    public String uploadUserProfileImageForCurrent(@NotNull MultipartFile file) {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());
        UserProfile profile = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
        String oldUrl = profile.getImageUrl();
        if (file.getSize() > Integer.parseInt(MINIMAL_ALLOWED_FILE_SIZE)) {
            try {
                file = Base64ToMultipartFileMapper.compressUploadedImage(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String url = replaceUserProfileImage(file, oldUrl, FileType.AVATAR);
        profile.setImageUrl(url);
        userProfileRepository.save(profile);
        return url;
    }

    private String replaceUserProfileImage(@NotNull MultipartFile file, String oldUrl, FileType fileType) {
        String url;
        try {
            var response = fileServiceClient.fileUpload(fileType, file);
            if (response.getBody() != null) {
                url = response.getBody().getUrl();
            } else {
                log.error("Can't get url of uploaded file");
                throw new FileUploadException("Upload File", "Can't get url of uploaded file");
            }
        } catch (Exception e) {
            log.error("Error while uploading file: {}", e.getMessage());
            throw new FileUploadException("Upload File ", e.getMessage());
        }
        // trying to remove old image
        removeProfileImage(oldUrl, Boolean.FALSE);
        return url;
    }

    private void removeProfileImage(String url, boolean throwError) {
        if (!StringUtils.isEmpty(url)) {
            try {
                fileServiceClient.deleteFile(url);
            } catch (Exception e) {
                log.error("Can't Delete OLD User Image url = {}", url);
                if (throwError) {
                    throw new FileUploadException(" deleting ", e.getMessage());
                }
            }
        }
    }

    @Transactional
    public void removeUserProfileImage() {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());

        UserProfile profile = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
        String oldUrl = profile.getImageUrl();
        removeProfileImage(oldUrl, Boolean.TRUE);
        profile.setImageUrl(null);
        userProfileRepository.save(profile);
    }

    @Transactional
    public String uploadUserProfileBannerForCurrent(@NotNull MultipartFile file) {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());

        UserProfile profile = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
        String oldUrl = profile.getImagePromoBannerUrl();
        // trying upload new image
        String url = replaceUserProfileImage(file, oldUrl, FileType.BANNER);
        profile.setImagePromoBannerUrl(url);
        userProfileRepository.save(profile);
        return url;
    }

    @Transactional
    public void removeUserProfileBanner() {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());

        UserProfile profile = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
        String oldUrl = profile.getImagePromoBannerUrl();
        removeProfileImage(oldUrl, Boolean.TRUE);
        profile.setImagePromoBannerUrl(null);
        userProfileRepository.save(profile);
    }

    @Transactional
    public List<UserProfileResponseDto> getUsersInfo(List<UUID> userIds) {
        List<UserProfile> userProfiles = userProfileRepository.findAllByIds(userIds);

        return userProfiles.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserProfileResponseDto> getUsersInfoByKeycloakIds(List<UUID> keycloakUserIds) {
        List<UserProfile> userProfiles = userProfileRepository.findByKeycloakUserIdIn(keycloakUserIds);

        return userProfiles.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public NftOwnerDto getOwnerInfo(UUID userId, OwnerType type, List<UUID> userIds) {

        String fullName = "";

        switch (type) {
            case CELEBRITY:
                Celebrity celebrity = celebrityRepository
                        .findById(userId)
                        .orElseThrow(() -> new ItemNotFoundException(Celebrity.class, userId));
                fullName = celebrity.getName() + (Objects.nonNull(celebrity.getLastName()) ? (" " + celebrity.getLastName()) : "");
                break;
            case FANAT:
                fullName = setFullName(userProfileRepository
                        .findByKeycloakUserId(userId)
                        .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, userId)));
                break;
        }

        return NftOwnerDto.builder()
                .name(fullName)
                .avatars(userProfileRepository.findImageIdsByUserIds(userIds))
                .build();
    }

    @Transactional
    public void attachCurrentUserToCelebrity(UUID celebrityId) {
        var currentUserId = UUID.fromString(securityUtil.getCurrentUser().getId());
        attachUserToCelebrity(currentUserId, celebrityId);
    }

    @Transactional
    public ProfileWallet attachUserToCelebrity(UUID keycloakUserId, UUID celebrityId) {
        var user = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new RestException("User %s was not found", HttpStatus.NOT_FOUND));
        Optional<ProfileWallet> alreadySubscribedCelebrity = user
                .getProfileWallets().stream()
                .filter(pw -> Objects.equals(celebrityId, pw.getCelebrity().getId()))
                .findFirst();
        if (alreadySubscribedCelebrity.isPresent()) {
            return alreadySubscribedCelebrity.get();
        }
        var celebrity = celebrityRepository.findById(celebrityId).orElseThrow(() ->
                new RestException(String.format("Celebrity %s was not found", celebrityId.toString()), HttpStatus.NOT_FOUND));
        return profileWalletService.createAndSaveProfileWallet(user, celebrity);
    }

    private String setFullName(UserProfile profile) {

        String fullName;
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();
        if (profile.isInvisibleName()) {
            fullName = profile.getNickname();
        } else {
            if (firstName.equals(null) || firstName.isEmpty() && lastName.equals(null) || lastName.isEmpty()) {
                fullName = "Unnamed";
            } else if (firstName.equals(null) || firstName.isEmpty()) {
                fullName = lastName;
            } else if (lastName.equals(null) || lastName.isEmpty()) {
                fullName = firstName;
            } else {
                fullName = profile.getFirstName() + " " + profile.getLastName();
            }
        }

        return fullName;
    }

    public Set<String> getUsersAvatars(List<UUID> userIds) {
        return userProfileRepository.findImageIdsByUserIds(userIds);
    }

    @Transactional(readOnly = true)
    public Map<UUID, ContentAuthorDto> getContentAuthorsMapByKeycloakIdIn(Set<UUID> authorKeycloakIds) {
        if (CollectionUtils.isEmpty(authorKeycloakIds)) {
            return Collections.emptyMap();
        }
        return userProfileRepository.findContentAuthorsByKeycloakIdIn(authorKeycloakIds)
                .collect(Collectors.toMap(ContentAuthorDto::getKeycloakId, Function.identity()));
    }

    @Transactional(readOnly = true)
    public Map<UUID, String> getModeratorNamesMapByKeycloakIds(@NotNull Set<UUID> moderatorKeycloakIds) {
        return userProfileRepository.findByKeycloakUserIdIn(moderatorKeycloakIds, ModeratorView.class)
                .collect(Collectors.toMap(ModeratorView::getKeycloakUserId, ModeratorView::getFullName));
    }

    public UUID exchangeUserIdForKeycloakId(@NonNull UUID userId) {
        return userProfileRepository.exchangeUserIdForKeycloakUserId(userId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, userId));
    }

}