package com.nft.platform.mapper;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.dto.request.CryptoWalletRequestDto;
import com.nft.platform.dto.response.CryptoWalletResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CryptoWalletMapper {

    CryptoWallet toEntity(CryptoWalletRequestDto requestDto, @MappingTarget CryptoWallet cryptoWallet);

    CryptoWalletResponseDto toDto(CryptoWallet cryptoWallet);

    @Mapping(target = "externalCryptoWalletId", expression = "java(getExternalCryptoWalletIdForUser(cryptoWallet.getExternalCryptoWalletId()))")
    CryptoWalletResponseDto toDtoForUser(CryptoWallet cryptoWallet);

    default String getExternalCryptoWalletIdForUser(String id) {
        return id.substring(0, 5) + "..." + id.substring(id.length() -4);
    }

}
