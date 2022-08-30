package com.nft.platform.controller;

import com.nft.platform.common.dto.PeriodResponseDto;
import com.nft.platform.common.dto.UserVoteReductionDto;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.dto.request.SubscriptionRequestDto;
import com.nft.platform.dto.request.UserRewardIncreaseDto;
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

    @GetMapping("/wheel-balance")
    @Operation(summary = "Find User available Spins by Celebrity")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public long findAvailableSpins(
            @RequestParam UUID keycloakUserId,
            @RequestParam UUID celebrityId
    ) {
        return profileWalletService.findAvailableSpins(keycloakUserId, celebrityId);
    }

    @GetMapping("/vote-balance")
    @Operation(summary = "Find User available Votes by Celebrity")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public long findAvailableVotes(
            @RequestParam UUID keycloakUserId,
            @RequestParam UUID celebrityId
    ) {
        return profileWalletService.findAvailableVotes(keycloakUserId, celebrityId);
    }

    @GetMapping("/nft-vote-balance")
    @Operation(summary = "Find User available Nft Votes by Celebrity")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public long findAvailableNftVotes(
            @RequestParam UUID keycloakUserId,
            @RequestParam UUID celebrityId
    ) {
        return profileWalletService.findAvailableNftVotes(keycloakUserId, celebrityId);
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

    @PostMapping("/vote-balance/decrement")
    @Operation(summary = "Decrement Vote Balance")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public void decrementVoteBalance(
            @Valid @RequestBody UserVoteReductionDto requestDto
    ) {
        profileWalletService.decrementVoteBalance(requestDto);
    }

    @PostMapping("/nft-vote-balance/decrement")
    @Operation(summary = "Decrement Vote Balance")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public void decrementNftVoteBalance(
            @Valid @RequestBody UserVoteReductionDto requestDto
    ) {
        profileWalletService.decrementNftVoteBalance(requestDto);
    }

    @PostMapping("/coins-addition")
    @Operation(summary = "Add Coins")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public void addCoins(
            @Valid @RequestBody UserRewardIncreaseDto requestDto
    ) {
        profileWalletService.addCoins(requestDto);
    }

    @PostMapping("/votes-addition")
    @Operation(summary = "Add Votes")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public void addVotes(
            @Valid @RequestBody UserRewardIncreaseDto requestDto
    ) {
        profileWalletService.addVotes(requestDto);
    }

    @PostMapping("/nft-votes-addition")
    @Operation(summary = "Add Nft Votes")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public void addNftVotes(
            @Valid @RequestBody UserRewardIncreaseDto requestDto
    ) {
        profileWalletService.addNftVotes(requestDto);
    }

}
