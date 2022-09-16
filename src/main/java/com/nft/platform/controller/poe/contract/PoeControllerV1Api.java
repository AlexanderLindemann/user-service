package com.nft.platform.controller.poe.contract;

import com.nft.platform.common.dto.PoeFilterDto;
import com.nft.platform.common.dto.PoeForUserDto;
import com.nft.platform.common.dto.PoeResponseDto;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Tag(name = "Poe Api")
@RequestMapping("/api/v1/poes")
public interface PoeControllerV1Api {

    @GetMapping("/{id}")
    @Operation(summary = "Get Poe by Id")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PoeResponseDto> findPoeById(@Parameter(name = "id", description = "Poe Id")
                                               @PathVariable(name = "id") UUID poeId
    );

    @GetMapping
    @Operation(summary = "Get Page of Poe by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    Page<PoeResponseDto> getPoesPage(@ParameterObject PoeFilterDto filter,
                                     @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC)
                                     @ParameterObject Pageable pageable
    );

    @GetMapping("/list")
    @Operation(summary = "Get Page of Poe by Page number and Page size")
    @ResponseStatus(HttpStatus.OK)
    List<PoeResponseDto> getPoesList(@RequestBody PoeFilterDto filter);

    @PostMapping("/list/for-user")
    @Operation(summary = "Get list of Poe for user")
    @ResponseStatus(HttpStatus.OK)
    List<PoeForUserDto> getPoesListForUser(@RequestBody PoeFilterDto filter,
                                           @RequestParam UUID userId,
                                           @RequestParam UUID celebrityId);

    @GetMapping("/for-user")
    @Operation(summary = "Get Poe for user")
    @ResponseStatus(HttpStatus.OK)
    Optional<PoeForUserDto> getPoeForUser(@RequestParam PoeAction poeAction,
                                          @RequestParam UUID userId,
                                          @RequestParam UUID celebrityId);

    @PostMapping
    @Operation(summary = "Create Poe")
    @ResponseStatus(HttpStatus.CREATED)
    PoeResponseDto createPoes(@Parameter(name = "PoeRequestDto", description = "Poe Request Dto")
                              @Valid
                              @RequestBody PoeRequestDto poeRequestDto
    );

    @PostMapping("/list")
    @Operation(summary = "Create Poe")
    @ResponseStatus(HttpStatus.CREATED)
    List<PoeResponseDto> createPoes(@Parameter(name = "PoeRequestDtos", description = "Poe Request Dtos")
                                    @Valid
                                    @RequestBody List<PoeRequestDto> poeRequestDto
    );

    @PutMapping("/{id}")
    @Operation(summary = "Update Poe")
    @ResponseStatus(HttpStatus.OK)
    PoeResponseDto updatePoe(@Parameter(name = "id", description = "Poe Id")
                             @PathVariable("id") UUID poeId,
                             @Parameter(name = "PoeRequestDto", description = "Poe Request Dto")
                             @Valid
                             @RequestBody PoeRequestDto poeRequestDto
    );


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Poe")
    @ResponseStatus(HttpStatus.OK)
    void deletePoe(@Parameter(name = "id", description = "Poe Id")
                   @PathVariable("id") UUID poeId
    );
}
