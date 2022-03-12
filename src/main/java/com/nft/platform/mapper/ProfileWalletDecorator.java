package com.nft.platform.mapper;

import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.dto.response.ProfileWalletResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class ProfileWalletDecorator implements ProfileWalletMapper {

    @Autowired
    @Qualifier("delegate")
    private ProfileWalletMapper delegate;

    @Autowired
    private CelebrityMapper celebrityMapper;

    @Override
    public ProfileWalletResponseDto toDto(ProfileWallet profileWallet) {
        ProfileWalletResponseDto dto = delegate.toDto(profileWallet);
        dto.setCelebrityDto(celebrityMapper.toDto(profileWallet.getCelebrity()));
        return dto;
    }

}
