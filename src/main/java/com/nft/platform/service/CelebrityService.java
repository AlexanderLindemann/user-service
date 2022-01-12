package com.nft.platform.service;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.mapper.CelebrityMapper;
import com.nft.platform.repository.CelebrityRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CelebrityService {

    private final CelebrityRepository celebrityRepository;
    private final CelebrityMapper mapper;

    @NonNull
    @Transactional(readOnly = true)
    public Optional<CelebrityResponseDto> findCelebrityById(@NonNull UUID id) {
        return celebrityRepository.findById(id)
                .map(mapper::toDto);
    }

    @NonNull
    @Transactional(readOnly = true)
    public Page<CelebrityResponseDto> getCelebrityPage(@NonNull Pageable pageable) {
        Page<Celebrity> celebrityPage = celebrityRepository.findAll(pageable);
        return celebrityPage.map(mapper::toDto);
    }

    @NonNull
    @Transactional
    public CelebrityResponseDto updateCelebrity(@NonNull UUID id, @NonNull CelebrityRequestDto requestDto) {
        Celebrity celebrity = celebrityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(Celebrity.class, id));
        if (!celebrity.getName().equals(requestDto.getName())) {
            log.info("Try update Celebrity name from {} to {}", celebrity.getName(), requestDto.getName());
            throwIfCelebrityNameExists(requestDto);
        }
        celebrity = mapper.toEntity(requestDto, celebrity);
        celebrityRepository.save(celebrity);
        return mapper.toDto(celebrity);
    }

    @NonNull
    @Transactional
    public CelebrityResponseDto createCelebrity(@NonNull CelebrityRequestDto requestDto) {
        throwIfCelebrityNameExists(requestDto);
        Celebrity celebrity = new Celebrity();
        celebrity = mapper.toEntity(requestDto, celebrity);
        celebrityRepository.save(celebrity);
        return mapper.toDto(celebrity);
    }

    private void throwIfCelebrityNameExists(@NonNull CelebrityRequestDto requestDto) {
        if (celebrityRepository.existsByNameIgnoreCase(requestDto.getName())) {
            throw new ItemConflictException(Celebrity.class, requestDto.getName());
        }
    }

}
