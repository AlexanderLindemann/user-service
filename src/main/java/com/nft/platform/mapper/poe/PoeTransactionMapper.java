package com.nft.platform.mapper.poe;

import com.nft.platform.common.event.ChallengeCompletedEvent;
import com.nft.platform.common.event.LikeAddedEvent;
import com.nft.platform.common.event.VoteCreatedEvent;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.event.ProfileWalletCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PoeTransactionMapper {

    PoeTransaction toEntity(PoeTransactionRequestDto requestDto, @MappingTarget PoeTransaction poeTransaction);

    PoeTransactionResponseDto toDto(PoeTransaction poeTransaction);

    PoeTransactionRequestDto toRequestDto(VoteCreatedEvent voteCreatedEvent);

    PoeTransactionRequestDto toRequestDto(ProfileWalletCreatedEvent profileWalletCreatedEvent);

    PoeTransactionRequestDto toRequestDto(LikeAddedEvent likeAddedEvent);

    PoeTransactionRequestDto toRequestDto(ChallengeCompletedEvent challengeCompletedEvent);
}
