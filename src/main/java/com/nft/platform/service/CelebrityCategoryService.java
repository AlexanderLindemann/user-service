package com.nft.platform.service;

import com.nft.platform.domain.CelebrityCategory;
import com.nft.platform.dto.request.CelebrityCategoryRequestDto;
import com.nft.platform.dto.response.CelebrityCategoryResponseDto;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.mapper.CelebrityCategoryMapper;
import com.nft.platform.repository.CelebrityCategoryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CelebrityCategoryService {

    private final CelebrityCategoryRepository celebrityCategoryRepository;
    private final CelebrityCategoryMapper mapper;

    @NonNull
    @Transactional(readOnly = true)
    public List<CelebrityCategoryResponseDto> getList() {
        return celebrityCategoryRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @NonNull
    @Transactional
    public CelebrityCategoryResponseDto createCelebrityCategory(@NonNull CelebrityCategoryRequestDto requestDto) {
        throwIfCelebrityCategoryNameExists(requestDto);
        CelebrityCategory category = new CelebrityCategory();
        category = mapper.toEntity(requestDto, category);
        celebrityCategoryRepository.save(category);
        return mapper.toDto(category);
    }

    @NonNull
    @Transactional
    public CelebrityCategoryResponseDto updateCelebrityCategory(@NonNull UUID id, @NonNull CelebrityCategoryRequestDto requestDto) {
        CelebrityCategory celebrityCategory = celebrityCategoryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(CelebrityCategory.class, id));
        if (!celebrityCategory.getName().equalsIgnoreCase(requestDto.getName())) {
            log.info("Try update Celebrity Category name from {} to {}", celebrityCategory.getName(), requestDto.getName());
            throwIfCelebrityCategoryNameExists(requestDto);
        }
        celebrityCategory = mapper.toEntity(requestDto, celebrityCategory);
        celebrityCategoryRepository.save(celebrityCategory);
        return mapper.toDto(celebrityCategory);
    }

    @Valid
    @Transactional
    public void deleteCelebrityCategory(@NotNull UUID categoryId) {
        if (!celebrityCategoryRepository.existsById(categoryId)) {
            throw new ItemNotFoundException(CelebrityCategory.class, categoryId);
        }
        celebrityCategoryRepository.deleteById(categoryId);
    }

    private void throwIfCelebrityCategoryNameExists(@NonNull CelebrityCategoryRequestDto requestDto) {
        if (celebrityCategoryRepository.existsByNameIgnoreCase(requestDto.getName())) {
            throw new ItemConflictException(CelebrityCategory.class, requestDto.getName());
        }
    }

}
