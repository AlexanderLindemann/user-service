package com.nft.platform.service;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.KeycloakUserIdWithCelebrityIdDto;
import com.nft.platform.dto.request.ProfileWalletRequestDto;
import com.nft.platform.dto.request.UserProfileFilterDto;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.UserProfileResponseDto;
import com.nft.platform.dto.response.UserProfileWithCelebrityIdsResponseDto;
import com.nft.platform.dto.response.UserProfileWithWalletResponseDto;
import com.nft.platform.dto.response.UserProfileWithWalletsResponseDto;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.mapper.UserProfileMapper;
import com.nft.platform.mapper.UserProfileWithCelebrityIdsMapper;
import com.nft.platform.mapper.UserProfileWithWalletsMapper;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.repository.CelebrityRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.repository.spec.UserProfileSpecifications;
import com.nft.platform.util.RLockKeys;
import com.nft.platform.util.security.SecurityUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final CelebrityRepository celebrityRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final UserProfileMapper mapper;
    private final UserProfileWithCelebrityIdsMapper mapperWithCelebrityIds;
    private final UserProfileWithWalletsMapper userProfileWithWalletsMapper;
    private final SecurityUtil securityUtil;
    private final SyncService syncService;

    @NonNull
    @Transactional(readOnly = true)
    public Optional<UserProfileWithWalletsResponseDto> findUserProfileById(@NonNull UUID id) {
        return userProfileRepository.findByIdWithWallets(id)
                .map(userProfileWithWalletsMapper::toDto);
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

    @Transactional(readOnly = true)
    public Optional<Integer> findUserVotes(UUID keycloakUserId, UUID celebrityId) {
        return profileWalletRepository.findVoteBalance(keycloakUserId, celebrityId);
    }

    @Transactional
    public int decrementUserVotes(KeycloakUserIdWithCelebrityIdDto requestDto) {
        String lockKey = RLockKeys.createVoteUpdateKey(requestDto.getKeycloakUserId(), requestDto.getCelebrityId());
        RLock rLock = syncService.getNamedLock(lockKey);
        return syncService.doSynchronized(rLock).run(() -> {
            Optional<Integer> voteBalance = profileWalletRepository
                    .findVoteBalance(requestDto.getKeycloakUserId(), requestDto.getCelebrityId());
            if (voteBalance.isEmpty()) {
                throw new RestException("ProfileWaller does not exists userId=" + requestDto.getKeycloakUserId() +
                        " celebrityId=" + requestDto.getCelebrityId(), HttpStatus.CONFLICT);
            }
            if (voteBalance.get() == 0) {
                throw new RestException("User has 0 votes userId=" + requestDto.getKeycloakUserId() +
                        " celebrityId=" + requestDto.getCelebrityId(), HttpStatus.CONFLICT);
            }
            return profileWalletRepository.decrementUserVotes(
                    requestDto.getKeycloakUserId(),
                    requestDto.getCelebrityId()
            );
        });
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

        Celebrity celebrity = celebrityRepository.findById(requestDto.getCelebrityId())
                .orElseGet(() -> {
                    log.error("Celebrity with ID = {} not found!", requestDto.getCelebrityId());
                    return null;
                });

        if (celebrity != null) {
            ProfileWallet profileWallet;
            Optional<ProfileWallet> existedProfileWallet = profileWalletRepository.findByUserProfileIdAndCelebrityId(userProfile.getId(), requestDto.getCelebrityId());
            // add new profileWallet if not exists
            if (existedProfileWallet.isEmpty()) {
                log.info("adding new connection with CELEBRITY_ID = {} for USER = {} with userProfileId = {}", requestDto.getCelebrityId(), userProfile.getUsername(), userProfile.getId());
                profileWallet = new ProfileWallet();
                profileWallet.setUserProfile(userProfile);
                profileWallet.setCelebrity(celebrity);
                profileWalletRepository.save(profileWallet);
            } else {
                log.info("ProfileWallet for CELEBRITY_ID = {} and USER = {} with userProfileId = {} existed", requestDto.getCelebrityId(), userProfile.getUsername(), userProfile.getId());
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
        ProfileWallet profileWallet = new ProfileWallet();
        profileWallet.setUserProfile(userProfile);
        profileWallet.setCelebrity(celebrity);

        profileWalletRepository.save(profileWallet);
    }

    @Transactional(readOnly = true)
    public Optional<UserProfileWithWalletResponseDto> findCurrentUserProfile() {
        var currentUser = securityUtil.getCurrentUser();
        Optional<UserProfile> userProfileO = userProfileRepository.findByKeycloakUserId(UUID.fromString(currentUser.getId()));
        if (userProfileO.isEmpty()) {
            return Optional.empty();
        }
        UserProfile userProfile = userProfileO.get();
        //TODO hardcode celebrityId = b5ecb411-a2a8-4a72-b9ab-3a64d8c70120
        UUID celebrityId = UUID.fromString("b5ecb411-a2a8-4a72-b9ab-3a64d8c70120");
        Optional<ProfileWallet> profileWalletO = profileWalletRepository.findByUserProfileIdAndCelebrityId(userProfile.getId(), celebrityId);
        if (profileWalletO.isEmpty()) {
            return Optional.empty();
        }
        ProfileWallet profileWallet = profileWalletO.get();
        var resultDto = userProfileRepository.findByKeycloakUserId(UUID.fromString(currentUser.getId()))
                .stream()
                .map(profile -> mapper.toDtoWithWallet(profile, profileWallet))
                .peek((e) -> e.setRoles(currentUser.getRoles()))
                .findAny();

        return resultDto;
    }

    @Transactional(readOnly = true)
    public Optional<UserProfileWithCelebrityIdsResponseDto> findUserProfileByKeycloakId(UUID keycloakId) {
        return userProfileRepository.findByKeycloakUserIdWithCelebrities(keycloakId)
                .map(mapperWithCelebrityIds::toDto);
    }

    @Transactional(readOnly = true)
    public boolean isConnectedWithCelebrity(KeycloakUserIdWithCelebrityIdDto requestDto) {
        return userProfileRepository.findByKeycloakUserIdAndCelebrityId(
                requestDto.getKeycloakUserId(), requestDto.getCelebrityId()).isPresent();
    }
}
