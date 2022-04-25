package com.nft.platform.mapper;

import com.nft.platform.domain.VotePrice;
import com.nft.platform.dto.response.VotePriceResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VotePriceMapper {

    VotePriceResponseDto toDto(VotePrice votePrice);
}
