package com.nft.platform.controller;

import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.request.CelebrityUpdateRequestDto;
import com.nft.platform.dto.response.*;
import com.nft.platform.service.CelebrityService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.nft.platform.util.security.RoleConstants.ROLE_TECH_TOKEN;
import static org.springframework.data.domain.Sort.Direction.ASC;

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
    @Operation(summary = "Get Page of Celebrity by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    public Page<CelebrityResponseDto> getCelebrityPage(
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return celebrityService.getCelebrityPage(pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Celebrity")
    @ResponseStatus(HttpStatus.OK)
    public CelebrityUpdateResponseDto updateCelebrityProfile(
            @Parameter(name = "id", description = "Celebrity Id")
            @PathVariable("id") UUID celebrityId,
            @Parameter(name = "celebrityRequestDto", description = "Celebrity Request Dto")
            @Valid @RequestBody CelebrityUpdateRequestDto celebrityRequestDto
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
    public Page<CelebrityResponseDto> getPopular(@RequestParam(defaultValue = "", required = false) String searchName, @PageableDefault(direction = ASC) Pageable pageable) {
        return celebrityService.getPopular(searchName, pageable);
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
