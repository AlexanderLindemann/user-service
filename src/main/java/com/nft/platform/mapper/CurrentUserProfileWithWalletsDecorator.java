package com.nft.platform.mapper;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.poe.request.UserBalanceRequestDto;
import com.nft.platform.dto.response.CurrentUserProfileWithWalletsResponseDto;
import com.nft.platform.dto.response.ProfileWalletResponseDto;
import com.nft.platform.service.CryptoWalletService;
import com.nft.platform.service.poe.PoeTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public abstract class CurrentUserProfileWithWalletsDecorator implements CurrentUserProfileWithWalletsMapper {

    @Autowired
    @Qualifier("delegate")
    private CurrentUserProfileWithWalletsMapper delegate;

    @Autowired
    private ProfileWalletMapper profileWalletMapper;

    @Autowired
    private CryptoWalletMapper cryptoWalletMapper;

    @Autowired
    private CryptoWalletService cryptoWalletService;

    @Autowired
    private PoeTransactionService poeTransactionService;

    @Override
    public CurrentUserProfileWithWalletsResponseDto toDto(UserProfile userProfile) {
        CurrentUserProfileWithWalletsResponseDto dto = delegate.toDto(userProfile);
        setUserProfileAllActivityBalance(userProfile.getKeycloakUserId(), dto);
        if (!CollectionUtils.isEmpty(userProfile.getProfileWallets())) {
            dto.setProfileWalletDto(userProfile.getProfileWallets().stream().findFirst().map(profileWalletMapper::toDto).orElse(null));
            setProfileWalletActivityBalance(userProfile.getKeycloakUserId(), dto.getProfileWalletDto());
        }
        if (userProfile.getCryptoWallets() != null) {
            dto.setCryptoWalletDtos(userProfile.getCryptoWallets().stream().map(cryptoWalletMapper::toDto).collect(Collectors.toList()));
        }
        String defaultWallet = userProfile.getDefaultCryptoWallet().map(CryptoWallet::getExternalCryptoWalletId).orElse(null);
        if (!StringUtils.isEmpty(defaultWallet)) {
            // trying to get balance for wallet
            BigDecimal balance = cryptoWalletService.getCryptoWalletBalance(defaultWallet);
            dto.getCryptoWalletDtos().stream().filter(c -> c.getExternalCryptoWalletId().equals(defaultWallet))
                    .findFirst()
                    .ifPresent(cryptoWalletResponseDto -> cryptoWalletResponseDto.setBalance(balance));
        }

        return dto;
    }

    private void setUserProfileAllActivityBalance(UUID keycloakUserId, CurrentUserProfileWithWalletsResponseDto dto) {
        Long userAllActivityBalance = poeTransactionService.calculateUserAllActivityBalance(
                UserBalanceRequestDto.builder()
                        .userId(keycloakUserId)
                        .build()
        );
        dto.setPointBalanceAll(userAllActivityBalance);
    }


    private void setProfileWalletActivityBalance(UUID keycloakUserId, ProfileWalletResponseDto profileWalletResponseDto) {
        Integer userActivityBalance = poeTransactionService.calculateUserActivityBalance(
                UserBalanceRequestDto.builder()
                        .userId(keycloakUserId)
                        .celebrityId(profileWalletResponseDto.getCelebrityDto().getId())
                        .build()
        );
        profileWalletResponseDto.setActivityBalance(userActivityBalance);
    }
}
