package com.nft.platform.controller;

import com.nft.platform.dto.poe.request.CelebrityFilterRequestDto;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.request.CelebrityUpdateRequestDto;
import com.nft.platform.dto.response.*;
import com.nft.platform.service.CelebrityService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.nft.platform.util.security.RoleConstants.ROLE_TECH_TOKEN;

@Tag(name = "Celebrity Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/celebrity")
public class CelebrityController {

    private final CelebrityService celebrityService;

    @GetMapping("/{id}")
    @Operation(summary = "Get Celebrity by Id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CelebrityResponseDto> findCelebrityById(
            @Parameter(name = "id", description = "Celebrity Id")
            @PathVariable(name = "id") UUID celebrityId
    ) {
        Optional<CelebrityResponseDto> celebrityResponseDtoO = celebrityService.findCelebrityById(celebrityId);
        return ResponseEntity.of(celebrityResponseDtoO);
    }

    @GetMapping("/links")
    @Operation(summary = "Get Celebrity Link")
    @ResponseStatus(HttpStatus.OK)
    public LinkCelebrityResponseDto getLinkCelebrity(@RequestParam(name = "celebrityId", required = false) UUID celebrityId) {
        return celebrityService.getCelebrityLink(celebrityId);
    }

    @GetMapping
    @Operation(summary = "Get Page of Celebrity by Filter and by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    public Page<CelebrityResponseDto> getCelebrityPage(
            @ParameterObject CelebrityFilterRequestDto celebrityFilterRequestDto,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return celebrityService.getCelebrityPage(celebrityFilterRequestDto, pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Celebrity")
    @ResponseStatus(HttpStatus.OK)
    public CelebrityUpdateResponseDto updateCelebrityProfile(
            @Parameter(name = "id", description = "Celebrity Id")
            @PathVariable("id") UUID celebrityId,
            @Parameter(name = "celebrityRequestDto", description = "Celebrity Request Dto")
            @Valid @RequestBody CelebrityRequestDto celebrityRequestDto
    ) {
        return celebrityService.updateCelebrity(celebrityId, celebrityRequestDto);
    }

    @PostMapping
    @Operation(summary = "Create Celebrity")
    @ResponseStatus(HttpStatus.CREATED)
    public CelebrityResponseDto createCelebrity(
            @Parameter(name = "celebrityRequestDto", description = "Celebrity Request Dto")
            @Valid @RequestBody CelebrityRequestDto celebrityRequestDto
    ) {
        return celebrityService.createCelebrity(celebrityRequestDto);
    }

    @GetMapping(path = "/showcase")
    @Operation(summary = "Get Celebrity and his last created Nft")
    @ResponseStatus(HttpStatus.OK)
    public List<CelebrityShowcaseResponseDto> getShowcase(@RequestParam Integer size) {
        return celebrityService.getShowcase(size);
    }

    @GetMapping(path = "/popular")
    @Operation(summary = "Get popular celebrities")
    @ResponseStatus(HttpStatus.OK)
    public List<CelebrityResponseDto> getPopular() {
        return celebrityService.getPopular();
    }

    @PutMapping("/{id}/theme")
    @Operation(summary = "Upload Theme for Celebrities")
    @ResponseStatus(HttpStatus.OK)
    public CelebrityThemeResponseDto uploadCelebrityTheme(@Parameter(name = "id", description = "Celebrity Id")
                                                          @PathVariable("id") UUID celebrityId,
                                                          @Parameter(name = "celebrityTheme", description = "Celebrity Theme")
                                                          @RequestBody String celebrityTheme) {
        return celebrityService.uploadCelebrityTheme(celebrityId, celebrityTheme);
    }

    @GetMapping(path = "/list")
    @Operation(summary = "Get list of Celebrities by their Ids")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    public Set<CelebrityResponseDto> getCelebrities(@RequestParam(name = "celebrityIds") Set<UUID> celebrityIds) {
        return celebrityService.getCelebrities(celebrityIds);
    }

    @GetMapping(path = "/names/map")
    @Operation(summary = "Get map of celebrity names by their Ids")
    @ResponseStatus(HttpStatus.OK)
    @Secured(ROLE_TECH_TOKEN)
    @Hidden
    public Map<UUID, String> getCelebritiesNamesMap(@RequestParam(name = "celebrityIds") Set<UUID> celebrityIds) {
        return celebrityService.getCelebritiesNamesMap(celebrityIds);
    }

    @GetMapping(path = "/active")
    @Operation(summary = "Get active celebrities Ids")
    @ResponseStatus(HttpStatus.OK)
    public List<UUID> getActiveCelebrityIds() {
        return celebrityService.getActiveCelebrityIds();
    }

    @Operation(summary = "Does celebrity exist by id")
    @GetMapping(path = "/existence")
    @Secured(ROLE_TECH_TOKEN)
    @ResponseStatus(HttpStatus.OK)
    public boolean existsById(@RequestParam UUID celebrityId) {
        return celebrityService.doesActiveCelebrityExistById(celebrityId);
    }
}
