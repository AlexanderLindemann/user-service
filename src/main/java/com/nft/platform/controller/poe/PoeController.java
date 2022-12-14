package com.nft.platform.controller.poe;

import com.nft.platform.common.dto.PoeFilterDto;
import com.nft.platform.common.dto.PoeForUserDto;
import com.nft.platform.common.dto.PoeResponseDto;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.controller.poe.contract.PoeControllerV1Api;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.service.poe.PoeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.nft.platform.util.security.RoleConstants.*;
import static java.util.Optional.ofNullable;

@RestController
@RequiredArgsConstructor
public class PoeController implements PoeControllerV1Api {

    private final PoeService poeService;

    @Override
    @Secured({ROLE_TECH_TOKEN, ROLE_ADMIN_PLATFORM, ROLE_ADMIN_CELEBRITY})
    public ResponseEntity<PoeResponseDto> findPoeById(UUID poeId) {
        return ResponseEntity.of(ofNullable(poeService.findById(poeId)));
    }

    @Override
    @Secured({ROLE_TECH_TOKEN, ROLE_ADMIN_PLATFORM, ROLE_ADMIN_CELEBRITY})
    public Page<PoeResponseDto> getPoesPage(PoeFilterDto filter, Pageable pageable) {
        return poeService.getPoesPage(filter, pageable);
    }

    @Override
    @Secured({ROLE_USER, ROLE_TECH_TOKEN, ROLE_ADMIN_PLATFORM, ROLE_ADMIN_CELEBRITY})
    public List<PoeResponseDto> getPoesList(PoeFilterDto filter) {
        return poeService.getPoesList(filter);
    }

    @Override
    @Secured({ROLE_TECH_TOKEN})
    public List<PoeForUserDto> getPoesListForUser(PoeFilterDto filter, UUID userId, UUID celebrityId) {
        return poeService.getPoesListForUser(filter, userId, celebrityId);
    }

    @Override
    @Secured({ROLE_TECH_TOKEN})
    public Optional<PoeForUserDto> getPoeForUser(PoeAction poeAction, UUID userId, UUID celebrityId) {
        return poeService.getPoeForUser(poeAction, userId, celebrityId);
    }

    @Override
    @Secured({ROLE_ADMIN_PLATFORM})
    public PoeResponseDto createPoes(@Valid PoeRequestDto poeRequestDto) {
        return poeService.createPoe(poeRequestDto);
    }

    @Override
    @Secured({ROLE_ADMIN_PLATFORM})
    public List<PoeResponseDto> createPoes(List<PoeRequestDto> poeRequestDto) {
        return poeService.createPoes(poeRequestDto);
    }

    @Override
    @Secured({ROLE_ADMIN_PLATFORM})
    public PoeResponseDto updatePoe(UUID poeId, @Valid PoeRequestDto poeRequestDto) {
        return poeService.updatePoe(poeId, poeRequestDto);
    }

    @Override
    @Secured({ROLE_ADMIN_PLATFORM})
    public void deletePoe(UUID poeId) {
        poeService.deletePoe(poeId);
    }
}
