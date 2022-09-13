package com.nft.platform.controller;

import com.nft.platform.dto.request.CryptoWalletRequestDto;
import com.nft.platform.dto.response.CryptoWalletResponseDto;
import com.nft.platform.service.CryptoWalletService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import java.util.List;
import java.util.UUID;

@Tag(name = "CryptoWallet Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crypto-wallet")
public class CryptoWalletController {

    private final CryptoWalletService cryptoWalletService;

    @GetMapping("/by-user-profile/{userProfileId}")
    @Operation(summary = "Get All Crypto Wallets By User Profile Id")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public List<CryptoWalletResponseDto> getCryptoWalletsByProfileId(@PathVariable("userProfileId") UUID userProfileId) {
        return cryptoWalletService.getAllByUserProfileId(userProfileId);
    }

    @GetMapping("/by-user-keycloak/{userKeycloakId}")
    @Operation(summary = "Get All Crypto Wallets By User Keycloak Id")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_TECH_TOKEN})
    public List<CryptoWalletResponseDto> getCryptoWalletsByKeycloakId(@PathVariable("userKeycloakId") UUID userKeycloakId) {
        return cryptoWalletService.getAllByUserKeycloakId(userKeycloakId);
    }

    @GetMapping
    @Operation(summary = "Get All current user's Crypto Wallets")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_USER})
    public List<CryptoWalletResponseDto> getCurrentUserCryptoWallets() {
        return cryptoWalletService.getCurrentUserCryptoWallets();
    }

    @PostMapping
    @Operation(summary = "Create new Crypto Wallet for User")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_USER, RoleConstants.ROLE_TECH_TOKEN})
    public CryptoWalletResponseDto createCryptoWallet(@Parameter(name = "CryptoWalletRequestDto", description = "Crypto Wallet Request Dto")
                                                      @Valid @RequestBody CryptoWalletRequestDto cryptoWalletRequestDto) {
        return cryptoWalletService.createWallet(cryptoWalletRequestDto);
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Make Crypto Wallet default")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_USER, RoleConstants.ROLE_TECH_TOKEN})
    public CryptoWalletResponseDto makeDefaultWallet(@Parameter(name = "id", description = "Crypto Wallet Id") @PathVariable("id") UUID walletId) {
        return cryptoWalletService.makeDefaultWallet(walletId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Crypto Wallet")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_USER, RoleConstants.ROLE_TECH_TOKEN})
    public void deleteCryptoWallet(@Parameter(name = "id", description = "Crypto Wallet Id") @PathVariable("id") UUID walletId) {
        cryptoWalletService.deleteCryptoWallet(walletId);
    }
}
