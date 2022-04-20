package com.nft.platform.service;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.response.CelebrityShowcaseResponseDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.dto.response.ShowcaseResponseDto;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.feign.client.NftServiceApiClient;
import com.nft.platform.mapper.CelebrityMapper;
import com.nft.platform.repository.CelebrityRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CelebrityService {

    private final CelebrityRepository celebrityRepository;
    private final CelebrityMapper mapper;
    private final NftServiceApiClient nftServiceApiClient;

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
        if (!celebrity.getName().equalsIgnoreCase(requestDto.getName())) {
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

    @Transactional(readOnly = true)
    public List<CelebrityShowcaseResponseDto> getShowcase(Integer size) {
        List<Celebrity> celebrities = celebrityRepository.findAll(PageRequest.of(0, size)).getContent();

        List<ShowcaseResponseDto> showcases = nftServiceApiClient.getShowcase(celebrities.stream()
                .map(Celebrity::getId)
                .collect(Collectors.toList()));

        return IntStream.range(0, celebrities.size())
                .mapToObj(i -> new CelebrityShowcaseResponseDto(
                        mapper.toNftDto(celebrities.get(i), showcases.get(i).getNftCount()), showcases.get(i).getNft()))
                .collect(Collectors.toList());
    }
}
