package com.nft.platform.mapper.poe;

import com.nft.platform.challengeservice.api.event.KafkaChallengeCompletedEvent;
import com.nft.platform.common.event.LikeAddedEvent;
import com.nft.platform.common.event.QuizCompletedEvent;
import com.nft.platform.common.event.VoteCreatedEvent;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.event.FirstAppOpenOnPeriodEvent;
import com.nft.platform.event.ProfileWalletCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PoeTransactionMapper {

    PoeTransaction toEntity(PoeTransactionRequestDto requestDto, @MappingTarget PoeTransaction poeTransaction);

    @Mapping(source = "poe", target = "poeId")
    PoeTransactionResponseDto toDto(PoeTransaction poeTransaction);

    default UUID map(Poe poe) {
        return poe.getId();
    }

    PoeTransactionRequestDto toRequestDto(VoteCreatedEvent voteCreatedEvent);

    PoeTransactionRequestDto toRequestDto(ProfileWalletCreatedEvent profileWalletCreatedEvent);

    PoeTransactionRequestDto toRequestDto(QuizCompletedEvent quizCompletedEvent);

    PoeTransactionRequestDto toRequestDto(LikeAddedEvent likeAddedEvent);

    PoeTransactionRequestDto toRequestDto(KafkaChallengeCompletedEvent challengeCompletedEvent);

    PoeTransactionRequestDto toRequestDto(FirstAppOpenOnPeriodEvent event);
}
