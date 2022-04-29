package com.nft.platform.mapper;

import com.nft.platform.domain.BundleForCoins;
import com.nft.platform.dto.response.BundleForCoinsResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BundleForCoinsMapper {

    BundleForCoinsResponseDto toDto(BundleForCoins bundleForCoins);
}
