package com.nft.platform.mapper;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.dto.request.CryptoWalletRequestDto;
import com.nft.platform.dto.response.CryptoWalletResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CryptoWalletMapper {

    @Mapping(target = "externalCryptoWalletId", expression = "java(requestDto.getExternalCryptoWalletId())")
    CryptoWallet toEntity(CryptoWalletRequestDto requestDto);

    CryptoWalletResponseDto toDto(CryptoWallet cryptoWallet);

    CryptoWalletResponseDto toDtoForUser(CryptoWallet cryptoWallet);

}
