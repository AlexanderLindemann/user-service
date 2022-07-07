package com.nft.platform.service;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.dto.response.CelebrityShowcaseResponseDto;
import com.nft.platform.dto.response.CelebrityThemeResponseDto;
import com.nft.platform.dto.response.NftCountResponseDto;
import com.nft.platform.dto.response.ShowcaseResponseDto;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.feign.client.NftServiceApiClient;
import com.nft.platform.mapper.CelebrityMapper;
import com.nft.platform.repository.CelebrityRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

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
        return celebrityRepository.findByIdAndActiveTrue(id)
                .map(mapper::toDto);
    }

    @NonNull
    @Transactional(readOnly = true)
    public Page<CelebrityResponseDto> getCelebrityPage(@NonNull Pageable pageable) {
        Page<Celebrity> celebrityPage = celebrityRepository.findAllByActiveTrue(pageable);
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
                .collect(toList()));

        return showcases.stream()
                .map(showcase -> mapper.toShowcaseDto(celebrities, showcase))
                .filter(showcase -> showcase.getNft() != null && showcase.getCelebrity() != null)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public Page<CelebrityResponseDto> getPopular(String searchName , Pageable pageable) {

        List<Celebrity> celebrities = celebrityRepository.findCelebritiesByNameContainsIgnoreCase(searchName);

        List<NftCountResponseDto> nftCountList = nftServiceApiClient.getNftCount(celebrities.stream()
            .map(Celebrity::getId)
            .collect(toList()));

        List<CelebrityResponseDto> sortedCelebritiesPopular = sortCelebritiesByNftCountList(celebrities, nftCountList).stream()
            .map(mapper::toDto)
            .collect(toList());

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), sortedCelebritiesPopular.size());

        return new PageImpl<>(sortedCelebritiesPopular.subList(start, end), pageable, sortedCelebritiesPopular.size());

    }

    private List<Celebrity> sortCelebritiesByNftCountList(List<Celebrity> celebrities,
                                                          List<NftCountResponseDto> nftCountList) {
        final Map<UUID, Integer> celebrityIdToIndexMap = range(0, nftCountList.size()).boxed()
                .collect(toMap(i -> nftCountList.get(i).getCelebrityId(), i -> i, (a, b) -> b));

        celebrities.sort(comparing(celebrity -> {
            val index = celebrityIdToIndexMap.get(celebrity.getId());
            return index != null ? index : celebrities.size() - 1;
        }));

        return celebrities;
    }


    public CelebrityThemeResponseDto uploadCelebrityTheme(UUID celebrityId, String celebrityTheme) {
        Celebrity celebrity = celebrityRepository.findById(celebrityId)
                .orElseThrow(() -> new ItemNotFoundException(Celebrity.class, celebrityId));
        celebrity.setJsonTheme(celebrityTheme);
        celebrityRepository.save(celebrity);

        return CelebrityThemeResponseDto.builder()
                .celebrityId(celebrityId)
                .theme(celebrityTheme)
                .build();
    }

    @Transactional(readOnly = true)
    public Set<CelebrityResponseDto> getCelebrities(Set<UUID> celebrityIds) {
        return celebrityRepository.findAllById(celebrityIds).stream()
                .map(mapper::toDto)
                .collect(Collectors.toSet());
    }
}
