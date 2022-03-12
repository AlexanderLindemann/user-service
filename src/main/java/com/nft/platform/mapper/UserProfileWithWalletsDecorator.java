package com.nft.platform.mapper;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.response.UserProfileWithWalletsResponseDto;
import com.nft.platform.feign.client.SolanaAdapterClient;
import com.nft.platform.feign.client.dto.WalletBalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.atomic.AtomicReference;
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
    private SolanaAdapterClient solanaAdapterClient;

    @Override
    public UserProfileWithWalletsResponseDto toDto(UserProfile userProfile) {
        UserProfileWithWalletsResponseDto dto = delegate.toDto(userProfile);
        if (userProfile.getProfileWallets() != null) {
            dto.setProfileWalletDtos(userProfile.getProfileWallets().stream().map(profileWalletMapper::toDto).collect(Collectors.toList()));
        }
        AtomicReference<String> defaultWallet = new AtomicReference<>();
        defaultWallet.set(null);
        if (userProfile.getCryptoWallets() != null) {
            dto.setCryptoWalletDtos(userProfile.getCryptoWallets().stream().map(cryptoWalletMapper::toDto).collect(Collectors.toList()));
            userProfile.getCryptoWallets().stream().filter(CryptoWallet::isDefaultWallet).findFirst().ifPresent(c -> defaultWallet.set(c.getExternalCryptoWalletId()));
        }
        if (!StringUtils.isEmpty(defaultWallet.get())) {
            // trying to get balance for wallet
            // TODO now only for SOLANA
            String wallet = defaultWallet.get();
            try {
                ResponseEntity<WalletBalanceResponse> balanceResponse = solanaAdapterClient.getWalletBalance(wallet);
                var cw = dto.getCryptoWalletDtos().stream().filter(c -> c.getExternalCryptoWalletId().equals(wallet)).findFirst();
                cw.ifPresent(cryptoWalletResponseDto -> cryptoWalletResponseDto.setBalance(balanceResponse.getBody().getBalance()));
            } catch (Exception e) {
                log.error("CAN'T get Crypto Balance for Wallet = {} \n Error = {}", wallet, e.getMessage());
            }
        }

        return dto;
    }
}
