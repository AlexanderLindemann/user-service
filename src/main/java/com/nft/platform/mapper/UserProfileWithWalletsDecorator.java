package com.nft.platform.mapper;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.response.UserProfileWithWalletsResponseDto;
import com.nft.platform.service.CryptoWalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Slf4j
public abstract class UserProfileWithWalletsDecorator implements UserProfileWithWalletsMapper {

    @Autowired
    @Qualifier("delegate")
    private UserProfileWithWalletsMapper delegate;

    @Autowired
    private ProfileWalletMapper profileWalletMapper;

    @Autowired
    private CryptoWalletMapper cryptoWalletMapper;

    @Autowired
    private CryptoWalletService cryptoWalletService;

    @Override
    public UserProfileWithWalletsResponseDto toDto(UserProfile userProfile) {
        UserProfileWithWalletsResponseDto dto = delegate.toDto(userProfile);
        if (userProfile.getProfileWallets() != null) {
            dto.setProfileWalletDtos(userProfile.getProfileWallets().stream().map(profileWalletMapper::toDto).collect(Collectors.toList()));
        }
        if (userProfile.getCryptoWallets() != null) {
            dto.setCryptoWalletDtos(userProfile.getCryptoWallets().stream().map(cryptoWalletMapper::toDto).collect(Collectors.toList()));
        }
        String defaultWallet = userProfile.getDefaultCryptoWallet().map(CryptoWallet::getExternalCryptoWalletId).orElse(null);
        // trying to get balance for wallet
        BigDecimal balance = cryptoWalletService.getCryptoWalletBalanceForUser(userProfile);
        if (!StringUtils.isEmpty(defaultWallet)) {
            dto.getCryptoWalletDtos().stream().filter(c -> c.getExternalCryptoWalletId().equals(defaultWallet))
                    .findFirst()
                    .ifPresent(cryptoWalletResponseDto -> cryptoWalletResponseDto.setBalance(balance));
        } else {
            dto.setTmpFanTokenBalance(balance);
        }

        return dto;
    }
}
