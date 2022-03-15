package com.nft.platform.service;

import com.nft.platform.common.enums.Blockchain;
import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.CryptoWalletRequestDto;
import com.nft.platform.dto.response.CryptoWalletResponseDto;
import com.nft.platform.exception.BadRequestException;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.mapper.CryptoWalletMapper;
import com.nft.platform.repository.CryptoWalletRepository;
import com.nft.platform.repository.UserProfileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoWalletService {

    private final CryptoWalletRepository cryptoWalletRepository;
    private final UserProfileRepository userProfileRepository;
    private final CryptoWalletMapper mapper;

    @NonNull
    @Transactional(readOnly = true)
    public List<CryptoWalletResponseDto> getAllByUserProfileId(@NonNull UUID userProfileId) {
        return cryptoWalletRepository.findAllByUserProfileId(userProfileId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @NonNull
    @Transactional(readOnly = true)
    public List<CryptoWalletResponseDto> getAllByUserKeycloakId(@NonNull UUID userKeycloakId) {
        return cryptoWalletRepository.findAllByUserProfileKeycloakUserId(userKeycloakId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @NonNull
    @Transactional
    public CryptoWalletResponseDto createWallet(@NonNull CryptoWalletRequestDto requestDto) {
        UserProfile userProfile;
        if (requestDto.getUserProfileId() != null) {
            userProfile = userProfileRepository.findById(requestDto.getUserProfileId())
                    .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, requestDto.getUserProfileId()));
        } else if (requestDto.getUserKeycloakId() != null) {
            userProfile = userProfileRepository.findByKeycloakUserId(requestDto.getUserKeycloakId())
                    .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, requestDto.getUserKeycloakId()));
        } else {
            throw new BadRequestException(CryptoWallet.class, "create", " can't get UserProfile without id");
        }
        if (ObjectUtils.isEmpty(requestDto.getBlockchain())) {
            // TODO now only SOLANA
            requestDto.setBlockchain(Blockchain.SOLANA);
        }
        if (cryptoWalletRepository.existsByExternalCryptoWalletIdAndBlockchain(requestDto.getExternalCryptoWalletId(), requestDto.getBlockchain())) {
            throw new ItemConflictException(CryptoWallet.class, requestDto.getExternalCryptoWalletId());
        }
        CryptoWallet wallet = new CryptoWallet();
        wallet = mapper.toEntity(requestDto, wallet);
        Set<CryptoWallet> existedWallets = userProfile.getCryptoWallets();
        if (CollectionUtils.isEmpty(existedWallets)) {
            // if it is first wallet = default
            wallet.setDefaultWallet(true);
        } else if (requestDto.isDefaultWallet()) {
            // if new wallet is default, old wallets need to be marked as default = false
            List<UUID> ids = existedWallets.stream().map(CryptoWallet::getId).collect(Collectors.toList());
            cryptoWalletRepository.setCryptoWalletsDefaultByIds(false, ids);
        }
        wallet.setUserProfile(userProfile);
        wallet = cryptoWalletRepository.save(wallet);
        return mapper.toDto(wallet);
    }

    @NonNull
    @Transactional
    public CryptoWalletResponseDto updateCryptoWallet(@NonNull UUID id, @NonNull CryptoWalletRequestDto requestDto) {
        CryptoWallet wallet = cryptoWalletRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(CryptoWallet.class, id));
        UserProfile userProfile = wallet.getUserProfile();
        if (!userProfile.getId().equals(requestDto.getUserProfileId())
                && !userProfile.getKeycloakUserId().equals(requestDto.getUserKeycloakId())) {
            throw new BadRequestException(CryptoWallet.class, wallet.getId(), "update", " belongs to other user");
        }
        wallet = mapper.toEntity(requestDto, wallet);
        wallet = cryptoWalletRepository.save(wallet);
        return mapper.toDto(wallet);
    }

    @Valid
    @Transactional
    public void deleteCryptoWallet(@NotNull UUID walletId) {
        if (!cryptoWalletRepository.existsById(walletId)) {
            throw new ItemNotFoundException(CryptoWallet.class, walletId);
        }
        cryptoWalletRepository.deleteById(walletId);
    }

}