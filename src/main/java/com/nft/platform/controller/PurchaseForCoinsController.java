package com.nft.platform.controller;

import com.nft.platform.common.enums.BundleType;
import com.nft.platform.dto.request.PurchaseForCoinsRequestDto;
import com.nft.platform.dto.response.BundleForCoinsResponseDto;
import com.nft.platform.service.BundleForCoinsService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Purchase for Coins Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bundle-for-coins")
public class PurchaseForCoinsController {

    private final BundleForCoinsService purchaseForCoinsService;

    @PostMapping("/buy")
    @Operation(summary = "Buy for coins")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({RoleConstants.ROLE_USER})
    public void buyBundleForCoins(@RequestBody PurchaseForCoinsRequestDto requestDto) {
        purchaseForCoinsService.buyBundleForCoins(requestDto);
    }

    @GetMapping("/bundles")
    @Operation(summary = "Get Bundles")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_ADMIN_CELEBRITY})
    public List<BundleForCoinsResponseDto> getBundles(@RequestParam BundleType type) {
        return purchaseForCoinsService.getBundles(type);
    }
}
