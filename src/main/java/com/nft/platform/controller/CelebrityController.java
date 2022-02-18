package com.nft.platform.controller;

import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import com.nft.platform.service.CelebrityService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

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
    public CelebrityResponseDto updateCelebrityProfile(
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
}