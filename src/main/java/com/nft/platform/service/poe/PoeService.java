package com.nft.platform.service.poe;

import com.nft.platform.common.dto.PoeForUserDto;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.dto.poe.request.PoeFilterDto;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.dto.poe.response.PoeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PoeService {

    PoeResponseDto findById(UUID id);

    Page<PoeResponseDto> getPoesPage(PoeFilterDto filter, Pageable pageable);

    List<PoeResponseDto> getPoesList(PoeFilterDto filter);

    List<PoeForUserDto> getPoesListForUser(PoeFilterDto filter, UUID userId, UUID celebrityId);

    Optional<PoeForUserDto> getPoeForUser(PoeAction poeAction, UUID userId, UUID celebrityId);

    PoeResponseDto createPoe(PoeRequestDto requestDto);

    List<PoeResponseDto> createPoes(List<PoeRequestDto> requestDtos);

    void deletePoe(UUID poeId);

    PoeResponseDto updatePoe(UUID poeId, PoeRequestDto poeRequestDto);
}
