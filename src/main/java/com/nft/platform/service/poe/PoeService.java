package com.nft.platform.service.poe;

import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.dto.poe.response.PoeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PoeService {

    PoeResponseDto findById(UUID id);

    Page<PoeResponseDto> getPoesPage(Pageable pageable);

    PoeResponseDto createPoe(PoeRequestDto requestDto);

    List<PoeResponseDto> createPoes(List<PoeRequestDto> requestDtos);

    void deletePoe(UUID poeId);

    PoeResponseDto updatePoe(UUID poeId, PoeRequestDto poeRequestDto);
}
