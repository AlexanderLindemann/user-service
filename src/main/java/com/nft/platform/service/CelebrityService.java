package com.nft.platform.service;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.view.CelebrityView;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.request.CelebrityUpdateRequestDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.dto.response.CelebrityShowcaseResponseDto;
import com.nft.platform.dto.response.CelebrityThemeResponseDto;
import com.nft.platform.dto.response.CelebrityUpdateResponseDto;
import com.nft.platform.dto.response.CurrentUserProfileWithWalletsResponseDto;
import com.nft.platform.dto.response.LinkCelebrityResponseDto;
import com.nft.platform.dto.response.NftCountResponseDto;
import com.nft.platform.dto.response.ShowcaseResponseDto;
import com.nft.platform.exception.ItemConflictException;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.feign.client.NftServiceApiClient;
import com.nft.platform.mapper.CelebrityMapper;
import com.nft.platform.repository.CelebrityRepository;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.util.security.SecurityUtil;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

@Service
@Slf4j
@RequiredArgsConstructor
public class CelebrityService {

    private final CelebrityRepository celebrityRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final UserProfileService userProfileService;
    private final CelebrityMapper mapper;
    private final NftServiceApiClient nftServiceApiClient;
    private final SecurityUtil securityUtil;

    @NonNull
    @Transactional(readOnly = true)
    public Optional<CelebrityResponseDto> findCelebrityById(@NonNull UUID id) {
        return celebrityRepository.findByIdAndActiveTrue(id)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public LinkCelebrityResponseDto getCelebrityLink(UUID id) {
        if (Objects.nonNull(id)) {
            Optional<Celebrity> celebrity = celebrityRepository.findById(id);
            if (celebrity.isPresent()) {
                return LinkCelebrityResponseDto.builder()
                        .androidLink(celebrity.get().getAndroidLink())
                        .iosLink(celebrity.get().getIosLink())
                        .build();
            }
        }
        return LinkCelebrityResponseDto.builder()
                .androidLink("https://ru.wikipedia.org/wiki")
                .iosLink("https://ru.wikipedia.org/wiki")
                .build();
    }


    @NonNull
    @Transactional
    public Page<CelebrityResponseDto> getCelebrityPage(@NonNull Pageable pageable) {
        Page<Celebrity> allCelebrity = celebrityRepository.findAllByActiveTrue(pageable);
        Page<CelebrityResponseDto> celebrityResponseDtos = allCelebrity.map(mapper::toDto);
        if (Objects.nonNull(securityUtil.getCurrentUserOrNull())) {
            Optional<CurrentUserProfileWithWalletsResponseDto> currentUserProfile = userProfileService.findCurrentUserProfile(null);
            if (currentUserProfile.isPresent()) {
                List<UUID> userSubscriptions = profileWalletRepository.findAllByUserProfileId(currentUserProfile.get().getId()).stream()
                        .map(ProfileWallet::getCelebrity)
                        .map(Celebrity::getId)
                        .collect(Collectors.toList());
                for (CelebrityResponseDto celebrityResponseDto : celebrityResponseDtos.getContent()) {
                    if (userSubscriptions.contains(celebrityResponseDto.getId())) {
                        celebrityResponseDto.setSubscribed(true);
                    }
                }
            }
        }
        return celebrityResponseDtos;
    }

    @NonNull
    @Transactional
    public CelebrityUpdateResponseDto updateCelebrity(@NonNull UUID id, @NonNull CelebrityUpdateRequestDto requestDto) {
        Celebrity celebrity = celebrityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(Celebrity.class, id));
        Object jsonTheme = celebrity.getJsonTheme();
        if (!celebrity.getName().equalsIgnoreCase(requestDto.getName())) {
            log.info("Try update Celebrity name from {} to {}", celebrity.getName(), requestDto.getName());
            throwIfCelebrityNameExists(requestDto);
        }
        celebrity = mapper.toEntity(requestDto, celebrity);
        celebrity.setJsonTheme(jsonTheme);
        celebrityRepository.save(celebrity);
        return mapper.toUpdateDto(celebrity);
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

    private void throwIfCelebrityNameExists(@NonNull CelebrityUpdateRequestDto requestDto) {
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
    public Page<CelebrityResponseDto> getPopular(String searchName, Pageable pageable) {

        List<Celebrity> celebrities = celebrityRepository.findCelebritiesByNameContainsIgnoreCaseAndActiveTrue(searchName);

        List<NftCountResponseDto> nftCountList = nftServiceApiClient.getNftCount(celebrities.stream()
                .map(Celebrity::getId)
                .collect(toList()));

        List<CelebrityResponseDto> sortedCelebritiesPopular = sortCelebritiesByNftCountList(celebrities, nftCountList).stream()
                .map(mapper::toDto)
                .collect(toList());

        final int start = (int) pageable.getOffset();
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

    @Transactional(readOnly = true)
    public Map<UUID, String> getCelebritiesNamesMap(Set<UUID> celebrityIds) {
        if (celebrityIds.isEmpty()) {
            return new HashMap<>();
        }
        return celebrityRepository.findByIdIn(celebrityIds)
                .collect(Collectors.toMap(CelebrityView::getId, view -> view.getNickName() == null ? EMPTY_STRING : view.getNickName()));
    }

    @Transactional(readOnly = true)
    public List<UUID> getActiveCelebrityIds() {
        return celebrityRepository.findAllByActiveIsTrue().stream()
                .map(Celebrity::getId)
                .collect(toList());
    }
}
