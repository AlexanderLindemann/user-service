package com.nft.platform.controller.poe;

import com.nft.platform.controller.poe.contract.PoeControllerV1Api;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.dto.poe.response.PoeResponseDto;
import com.nft.platform.service.poe.PoeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static com.nft.platform.util.security.RoleConstants.ROLE_ADMIN_CELEBRITY;
import static com.nft.platform.util.security.RoleConstants.ROLE_TECH_TOKEN;
import static com.nft.platform.util.security.RoleConstants.ROLE_USER;
import static java.util.Optional.ofNullable;

@RestController
@RequiredArgsConstructor
public class PoeController implements PoeControllerV1Api {

    private final PoeService poeService;

    @Override
    @Secured({ROLE_USER, ROLE_TECH_TOKEN, ROLE_ADMIN_CELEBRITY})
    public ResponseEntity<PoeResponseDto> findPoeById(UUID poeId) {
        return ResponseEntity.of(ofNullable(poeService.findById(poeId)));
    }

    @Override
    @Secured({ROLE_USER, ROLE_TECH_TOKEN, ROLE_ADMIN_CELEBRITY})
    public Page<PoeResponseDto> getPoesPage(Pageable pageable) {
        return poeService.getPoesPage(pageable);
    }

    @Override
    @Secured({ROLE_ADMIN_CELEBRITY})
    public PoeResponseDto createPoes(@Valid PoeRequestDto poeRequestDto) {
        return poeService.createPoe(poeRequestDto);
    }

    @Override
    @Secured({ROLE_ADMIN_CELEBRITY})
    public List<PoeResponseDto> createPoes(List<PoeRequestDto> poeRequestDto) {
        return poeService.createPoes(poeRequestDto);
    }

    @Override
    @Secured({ROLE_ADMIN_CELEBRITY})
    public PoeResponseDto updatePoe(UUID poeId, @Valid PoeRequestDto poeRequestDto) {
        return poeService.updatePoe(poeId, poeRequestDto);
    }

    @Override
    @Secured({ROLE_ADMIN_CELEBRITY})
    public void deletePoe(UUID poeId) {
        poeService.deletePoe(poeId);
    }
}
