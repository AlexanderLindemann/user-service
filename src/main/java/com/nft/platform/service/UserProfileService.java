package com.nft.platform.service;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.ProfileWalletRequestDto;
import com.nft.platform.dto.request.UserProfileRequestDto;
import com.nft.platform.dto.response.UserProfileResponseDto;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.mapper.UserProfileMapper;
import com.nft.platform.repository.CelebrityRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.repository.UserProfileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private static final String TEST_CELEBRITY_NAME = "test-celebrity";

    private final UserProfileRepository userProfileRepository;
    private final CelebrityRepository celebrityRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final UserProfileMapper mapper;

    @NonNull
    @Transactional(readOnly = true)
    public Optional<UserProfileResponseDto> findUserProfileById(@NonNull UUID id) {
        return userProfileRepository.findById(id)
                .map(mapper::toDto);
    }

    @NonNull
    @Transactional(readOnly = true)
    public Page<UserProfileResponseDto> getUserProfilePage(@NonNull Pageable pageable) {
        Page<UserProfile> userProfilePage = userProfileRepository.findAll(pageable);
        return userProfilePage.map(mapper::toDto);
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
    public UserProfileResponseDto createUserProfile(@NonNull UserProfileRequestDto requestDto) {
        Celebrity celebrity = celebrityRepository.findById(requestDto.getCelebrityId())
                .orElseThrow(() -> new ItemNotFoundException(Celebrity.class, requestDto.getCelebrityId()));
        UserProfile userProfile = new UserProfile();
        userProfile = mapper.toEntity(requestDto, userProfile);

        ProfileWallet profileWallet = new ProfileWallet();
        profileWallet.setUserProfile(userProfile);
        profileWallet.setCelebrity(celebrity);

        profileWalletRepository.save(profileWallet);
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
}
