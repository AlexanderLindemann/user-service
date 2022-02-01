package com.nft.platform.controller;

import com.nft.platform.dto.request.ProfileWalletPeriodUpdateDto;
import com.nft.platform.service.ProfileWalletService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Profile Wallet Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile-wallet")
public class ProfileWalletController {

    private final ProfileWalletService profileWalletService;

    @PutMapping
    @Operation(summary = "Update Profile Wallet On Period If Needed")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN_CELEBRITY, RoleConstants.ROLE_ADMIN_PLATFORM})
    public boolean updateProfileWalletOnPeriodIfNeeded(@RequestBody ProfileWalletPeriodUpdateDto requestDto) {
        return profileWalletService.updateProfileWalletOnPeriodIfNeeded(requestDto);
    }

}
