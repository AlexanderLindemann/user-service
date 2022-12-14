package com.nft.platform.service;

import com.nft.platform.common.enums.Blockchain;
import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.CryptoWalletRequestDto;
import com.nft.platform.dto.response.CryptoWalletResponseDto;
import com.nft.platform.event.TmpFanTokenDistributionEvent;
import com.nft.platform.exception.BadRequestException;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.feign.client.SolanaAdapterClient;
import com.nft.platform.feign.client.dto.WalletFanTokenBalanceResponse;
import com.nft.platform.mapper.CryptoWalletMapper;
import com.nft.platform.repository.CryptoWalletRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.util.CryptoConstants;
import com.nft.platform.util.security.SecurityUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
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
    private final SolanaAdapterClient solanaAdapterClient;
    private final SecurityUtil securityUtil;
    private final FanTokenDistributionTransactionService fanTokenDistributionTransactionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @NonNull
    @Transactional(readOnly = true)
    public List<CryptoWalletResponseDto> getAllByUserProfileId(@NonNull UUID userProfileId) {
        return cryptoWalletRepository.findAllByUserProfileIdOrderByCreatedAt(userProfileId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @NonNull
    @Transactional(readOnly = true)
    public List<CryptoWalletResponseDto> getAllByUserKeycloakId(@NonNull UUID userKeycloakId) {
        return cryptoWalletRepository.findAllByUserProfileKeycloakUserIdOrderByCreatedAt(userKeycloakId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CryptoWalletResponseDto> getCurrentUserCryptoWallets() {
        UUID currentKeycloakUserId = securityUtil.getCurrentUserId();
        return cryptoWalletRepository.findAllByUserProfileKeycloakUserIdOrderByCreatedAt(currentKeycloakUserId)
                .stream()
                .map(mapper::toDtoForUser)
                .collect(Collectors.toList());
    }

    @NonNull
    @Transactional
    public CryptoWalletResponseDto createWallet(@NonNull CryptoWalletRequestDto requestDto) {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());
        UserProfile userProfile = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));

        if (ObjectUtils.isEmpty(requestDto.getBlockchain())) {
            // TODO now only SOLANA
            requestDto.setBlockchain(Blockchain.SOLANA);
        }
        if (cryptoWalletRepository.existsByExternalCryptoWalletIdAndBlockchain(requestDto.getExternalCryptoWalletId(), requestDto.getBlockchain())) {
            throw new ItemConflictException(CryptoWallet.class, requestDto.getExternalCryptoWalletId(), "Wallet already exists in system");
        }

        // checking wallet
        checkCryptoWalletBeforeAdd(requestDto, Boolean.TRUE, null);

        CryptoWallet  wallet = mapper.toEntity(requestDto);
        Set<CryptoWallet> existedWallets = userProfile.getCryptoWallets();
        if (CollectionUtils.isEmpty(existedWallets)) {
            // if it is first wallet = default
            wallet.setDefaultWallet(true);
            TmpFanTokenDistributionEvent event = TmpFanTokenDistributionEvent.builder()
                    .keycloakUserId(keycloakUserId)
                    .build();
            applicationEventPublisher.publishEvent(event);
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
    public CryptoWalletResponseDto makeDefaultWallet(@NonNull UUID id) {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());

        int updatedRowCount = cryptoWalletRepository.setCryptoWalletDefaultTrue(id, keycloakUserId);
        if (updatedRowCount != 1) {
            throw new ItemNotFoundException(CryptoWallet.class, id);
        }

        cryptoWalletRepository.setCryptoWalletsDefaultFalseExcludeId(id, keycloakUserId);

        return mapper.toDto(cryptoWalletRepository.getById(id));
    }

    private void checkCryptoWalletBeforeAdd(@NonNull CryptoWalletRequestDto requestDto, boolean isForCreate, UUID idIfUpdate) {
        if (isForCreate && cryptoWalletRepository.existsByExternalCryptoWalletIdAndBlockchain(requestDto.getExternalCryptoWalletId(), requestDto.getBlockchain())) {
            throw new ItemConflictException(CryptoWallet.class, requestDto.getExternalCryptoWalletId(), "Wallet already exists in system");
        } else if (!isForCreate) {
            var existed = cryptoWalletRepository.findByExternalCryptoWalletIdAndBlockchain(requestDto.getExternalCryptoWalletId(), requestDto.getBlockchain());
            if (existed.isPresent() && !existed.get().getId().equals(idIfUpdate)) {
                throw new ItemConflictException(CryptoWallet.class, requestDto.getExternalCryptoWalletId(), "Wallet already exists in system");
            }
        }

        // checking wallet in blockchain
        try {
            // TODO now it is only for SOLANA
            var resp = solanaAdapterClient.getWalletInfo(requestDto.getExternalCryptoWalletId());
            if (resp.getBody() != null && Boolean.FALSE.equals(resp.getBody().getIsSolana())) {
                log.error("Can't create wallet {}, wallet doesn't exists in blockchain", requestDto.getExternalCryptoWalletId());
                throw new BadRequestException(CryptoWallet.class, "create wallet", "wallet doesn't exists in blockchain");
            }
        } catch (Exception e) {
            log.error("Can't create wallet: Can't get info for wallet with publicKey {}; \n error = {}", requestDto.getExternalCryptoWalletId(), e.getMessage());
            throw new RestException("Can't get info for wallet with publicKey from blockchain = " + requestDto.getExternalCryptoWalletId(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Valid
    @Transactional
    public void deleteCryptoWallet(@NotNull UUID walletId) {
        var currentUser = securityUtil.getCurrentUser();
        UUID keycloakUserId = UUID.fromString(currentUser.getId());
        if (!cryptoWalletRepository.existsById(walletId)) {
            throw new ItemNotFoundException(CryptoWallet.class, walletId);
        }
        var cryptoWallet = cryptoWalletRepository.getById(walletId);
        if (keycloakUserId.equals(cryptoWallet.getUserProfile().getKeycloakUserId())) {
            cryptoWalletRepository.deleteById(walletId);
        } else {
            log.error("Failed Deleting: Wallet = {} belongs to other user", walletId);
            throw new BadRequestException(CryptoWallet.class, walletId, "delete", " belongs to other user");
        }
    }

    @Valid
    @Transactional
    public BigDecimal getCryptoWalletBalanceForUser(@NotNull UserProfile userProfile) {
        BigDecimal balance = BigDecimal.ZERO;

        String defaultWallet = userProfile.getDefaultCryptoWallet().map(CryptoWallet::getExternalCryptoWalletId).orElse(null);
        if (!StringUtils.isEmpty(defaultWallet)) {
            // trying to get balance for wallet
            balance = getCryptoWalletBalance(defaultWallet);
        } else if (!userProfile.isHasCryptoWallets()) {
            balance = BigDecimal.valueOf(
                    fanTokenDistributionTransactionService.getTmpFanTokenBalanceForUser(userProfile).orElse(0L));
        }
        return balance.multiply(CryptoConstants.TRANSFER_FROM_LAMP_TO_FAN_TOKEN);
    }

    @Valid
    public BigDecimal getCryptoWalletBalance(@NotNull String walletId) {
        BigDecimal balance = BigDecimal.ZERO;
        try {
            // TODO now only for SOLANA
            ResponseEntity<WalletFanTokenBalanceResponse> balanceResponse = solanaAdapterClient.getWalletFanBalance(walletId);
            balance = balanceResponse.getBody() != null ? balanceResponse.getBody().getTokenBalance() : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("CAN'T get Crypto Balance for Wallet = {} \n Error = {}", walletId, e.getMessage());
        }
        return balance;
    }

}
