package com.nft.platform.mapper.poe;

import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PoeTransactionMapper {

    PoeTransaction toEntity(PoeTransactionRequestDto requestDto, @MappingTarget PoeTransaction poeTransaction);

    PoeTransactionResponseDto toDto(PoeTransaction poeTransaction);
}
