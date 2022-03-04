package com.nft.platform.controller;

import com.nft.platform.dto.request.CelebrityCategoryRequestDto;
import com.nft.platform.dto.response.CelebrityCategoryResponseDto;
import com.nft.platform.service.CelebrityCategoryService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Celebrity Category Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/celebrity-category")
public class CelebrityCategoryController {

    private final CelebrityCategoryService celebrityCategoryService;

    @GetMapping
    @Operation(summary = "Get List of Celebrity Categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CelebrityCategoryResponseDto> getCelebrityCategoryList() {
        return celebrityCategoryService.getList();
    }

    @PostMapping
    @Operation(summary = "Create Celebrity Category")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public CelebrityCategoryResponseDto createCelebrityCategory(@Parameter(name = "celebrityCategoryRequestDto", description = "Celebrity Category Request Dto")
                                                                @Valid @RequestBody CelebrityCategoryRequestDto celebrityCategoryRequestDto) {
        return celebrityCategoryService.createCelebrityCategory(celebrityCategoryRequestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Celebrity Category")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public CelebrityCategoryResponseDto updateCelebrityCategory(@Parameter(name = "id", description = "Celebrity Category Id") @PathVariable("id") UUID celebrityCategoryId,
                                                                @Parameter(name = "celebrityCategoryRequestDto", description = "Celebrity Category Request Dto")
                                                                @Valid @RequestBody CelebrityCategoryRequestDto celebrityCategoryRequestDto) {
        return celebrityCategoryService.updateCelebrityCategory(celebrityCategoryId, celebrityCategoryRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Celebrity Category")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public void deleteCelebrityCategory(@Parameter(name = "id", description = "Celebrity Category Id") @PathVariable("id") UUID celebrityCategoryId) {
        celebrityCategoryService.deleteCelebrityCategory(celebrityCategoryId);
    }


}
