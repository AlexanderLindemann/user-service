package com.nft.platform.controller;

import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.dto.request.ProfileWalletVotesDto;
import com.nft.platform.dto.request.SubscriptionRequestDto;
import com.nft.platform.dto.request.UserVoteReductionDto;
import com.nft.platform.dto.response.PeriodResponseDto;
import com.nft.platform.dto.response.VotePriceResponseDto;
import com.nft.platform.service.PeriodService;
import com.nft.platform.service.ProfileWalletService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Profile Wallet Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile-wallet")
public class ProfileWalletController {

    private final ProfileWalletService profileWalletService;
    private final PeriodService periodService;

    @GetMapping("/is-subscriber")
    @Operation(summary = "Is User subscriber")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public boolean isUserSubscriber(@RequestParam UUID keycloakUserId,
                                    @RequestParam UUID celebrityId) {
        return profileWalletService.isUserSubscriber(keycloakUserId, celebrityId);
    }

    @PutMapping("/subscription")
    @Operation(summary = "Update subscription status")
    @ResponseStatus(HttpStatus.OK)
    public void updateSubscriptionStatus(@RequestBody SubscriptionRequestDto requestDto) {
        profileWalletService.updateSubscriptionStatus(requestDto);
    }

    @PutMapping
    @Operation(summary = "Update Profile Wallet On Period If Needed")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_USER})
    public ResponseEntity<PeriodResponseDto> updateProfileWalletOnPeriodIfNeeded() {
        profileWalletService.updateProfileWalletOnPeriodIfNeeded();
        return ResponseEntity.of(periodService.findPeriod(PeriodStatus.NEXT));
    }

    @PostMapping("/buy-votes")
    @Operation(summary = "Buy votes for coins")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({RoleConstants.ROLE_USER})
    public void buyVotesForCoins(@RequestBody ProfileWalletVotesDto requestDto) {
        profileWalletService.buyVotesForCoins(requestDto);
    }

    @GetMapping("/vote-bundles")
    @Operation(summary = "Get Vote Bundles")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN_PLATFORM, RoleConstants.ROLE_ADMIN_CELEBRITY})
    public List<VotePriceResponseDto> getVoteBundles() {
        return profileWalletService.getVoteBundles();
    }

    @GetMapping("/wheel-balance")
    @Operation(summary = "Find Wheel Balance")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public ResponseEntity<Integer> findWheelBalance(
            @RequestParam("keycloakUserId") UUID keycloakUserId,
            @RequestParam UUID celebrityId
    ) {
        Optional<Integer> wheelBalance0 = profileWalletService.findWheelBalance(keycloakUserId, celebrityId);
        return ResponseEntity.of(wheelBalance0);
    }

    @PostMapping("/wheel-balance/decrement")
    @Operation(summary = "Decrement Wheel Balance")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public void decrementWheelBalance(
            @Valid @RequestBody UserVoteReductionDto requestDto
    ) {
        profileWalletService.decrementWheelBalance(requestDto);
    }
}
