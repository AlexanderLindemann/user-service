package com.nft.platform.service.poe.impl;

import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.poe.request.PoeFilterDto;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.dto.poe.response.PoeResponseDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.mapper.IMapper;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.repository.spec.PoeSpecifications;
import com.nft.platform.service.poe.PoeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PoeServiceImpl implements PoeService {

    private final PoeRepository poeRepository;
    private final IMapper<Poe, PoeResponseDto> poeResponseDtoIMapper;
    private final IMapper<Poe, PoeRequestDto> poeRequestDtoIMapper;

    @Override
    @Transactional(readOnly = true)
    public PoeResponseDto findById(UUID id) {
        val poe = poeRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(Poe.class, id));
        return poeResponseDtoIMapper.convert(poe);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PoeResponseDto> getPoesPage(PoeFilterDto filter, Pageable pageable) {
        Specification<Poe> spec = PoeSpecifications.from(filter);
        val poePage = poeRepository.findAll(spec, pageable);
        return poePage.map(poeResponseDtoIMapper::convert);
    }

    @Override
    @Transactional
    public PoeResponseDto createPoe(PoeRequestDto requestDto) {
        var poe = poeRequestDtoIMapper.reverse(requestDto);
        val saved = poeRepository.save(poe);
        return poeResponseDtoIMapper.convert(saved);
    }

    @Override
    public List<PoeResponseDto> createPoes(List<PoeRequestDto> requestDtos) {
        return requestDtos
                .stream()
                .map(this::createPoe)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePoe(UUID poeId) {
        poeRepository.deleteById(poeId);
    }

    @Transactional
    @Override
    public PoeResponseDto updatePoe(UUID poeId, PoeRequestDto poeRequestDto) {
        var poe = poeRepository.findById(poeId)
                .orElseThrow(() -> new ItemNotFoundException(Poe.class, poeId));
        poe = poeRequestDtoIMapper.reverse(poeRequestDto, poe);
        return poeResponseDtoIMapper.convert(poe);
    }
}
