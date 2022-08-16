package com.nft.platform.service.poe.impl;

import com.nft.platform.common.dto.PoeFilterDto;
import com.nft.platform.common.dto.PoeForUserDto;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.dto.poe.response.PoeResponseDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.mapper.IMapper;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.repository.spec.PoeSpecifications;
import com.nft.platform.service.ProfileWalletService;
import com.nft.platform.service.poe.PoeService;
import com.nft.platform.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PoeServiceImpl implements PoeService {

    private final PoeRepository poeRepository;
    private final IMapper<Poe, PoeResponseDto> poeResponseDtoIMapper;
    private final IMapper<Poe, PoeRequestDto> poeRequestDtoIMapper;
    private final ProfileWalletService profileWalletService;

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
    @Transactional(readOnly = true)
    public List<PoeResponseDto> getPoesList(PoeFilterDto filter) {
        Specification<Poe> spec = PoeSpecifications.from(filter);
        val poes = poeRepository.findAll(spec);
        return poes.stream().map(poeResponseDtoIMapper::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoeForUserDto> getPoesListForUser(PoeFilterDto filter, UUID userId, UUID celebrityId) {
        Specification<Poe> spec = PoeSpecifications.from(filter);
        val poes = poeRepository.findAll(spec);
        profileWalletService.isUserSubscriber(userId, celebrityId);
        boolean subscriber = profileWalletService.isUserSubscriber(userId, celebrityId);
        return poes.stream()
                .map(poe -> mapPoeToPoeForUserDto(poe, subscriber))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PoeForUserDto> getPoeForUser(PoeAction poeAction, UUID userId, UUID celebrityId) {
        Specification<Poe> spec = PoeSpecifications.codeEqual(poeAction);
        Optional<Poe> poeO = poeRepository.findOne(spec);
        boolean subscriber = profileWalletService.isUserSubscriber(userId, celebrityId);
        return poeO.map(poe -> mapPoeToPoeForUserDto(poe, subscriber));
    }

    private PoeForUserDto mapPoeToPoeForUserDto(Poe poe, boolean subscriber) {
        int coins = CommonUtils.toPrimitive(poe.getCoinsReward());
        int points = CommonUtils.toPrimitive(poe.getPointsReward());
        if (subscriber) {
            coins += CommonUtils.toPrimitive(poe.getCoinsRewardSub());
            points += CommonUtils.toPrimitive(poe.getPointsRewardSub());
        }
        return PoeForUserDto.builder()
                .coinsReward(coins)
                .pointsReward(points)
                .code(poe.getCode())
                .build();
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
