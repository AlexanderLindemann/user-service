package com.nft.platform.mapper;

import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.dto.response.ProfileWalletResponseDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = CelebrityMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(ProfileWalletDecorator.class)
public interface ProfileWalletMapper {

    ProfileWalletResponseDto toDto(ProfileWallet profileWallet);
}
