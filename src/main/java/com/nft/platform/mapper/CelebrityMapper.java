package com.nft.platform.mapper;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.CelebrityCategory;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.response.CelebrityNftResponseDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.dto.response.CelebrityShowcaseResponseDto;
import com.nft.platform.dto.response.ShowcaseResponseDto;
import com.nft.platform.exception.RestException;
import com.nft.platform.repository.CelebrityCategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class CelebrityMapper {

    @Lazy
    @Autowired
    private CelebrityCategoryRepository celebrityCategoryRepository;

    public CelebrityCategory celebrityCategoryIdToCelebrityCategory(UUID categoryId) {
        return celebrityCategoryRepository.findById(categoryId).orElseThrow(() -> new RestException("Celebrity category '" + categoryId + "' not found!", HttpStatus.NOT_FOUND));
    }

    public abstract Celebrity toEntity(CelebrityRequestDto requestDto, @MappingTarget Celebrity celebrity);

    @Mapping(target = "name", qualifiedByName = "getFullName")
    public abstract CelebrityResponseDto toDto(Celebrity celebrity);

    public abstract CelebrityNftResponseDto toNftDto(Celebrity celebrity, Integer nftCount);

    public CelebrityShowcaseResponseDto toShowcaseDto(List<Celebrity> celebrities, ShowcaseResponseDto showcase) {
        CelebrityNftResponseDto celebrity = toNftDto(
                getCelebrity(celebrities, showcase.getCelebrityId()), showcase.getNftCount()
        );

        var nft = showcase.getNft();
        nft.setCelebrityId(celebrity.getId());
        nft.setCelebrityName(celebrity.getName());

        return new CelebrityShowcaseResponseDto(celebrity, nft);
    }

    @Named("getFullName")
    static String getFullName(Celebrity celebrity) {
        return celebrity.getName() + " " + (Objects.nonNull(celebrity.getLastName()) ? celebrity.getLastName() : "");
    }

    private Celebrity getCelebrity(List<Celebrity> celebrities, UUID celebrityId) {
        return celebrities.stream()
                .filter(celebrity -> celebrity.getId().equals(celebrityId))
                .findFirst().orElse(null);
    }
}
