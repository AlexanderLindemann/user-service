package com.nft.platform.controller.poe.contract;

import com.nft.platform.dto.poe.request.PoeFilterDto;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.dto.poe.response.PoeResponseDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
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
