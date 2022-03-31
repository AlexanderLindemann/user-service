package com.nft.platform.controller.poe;

import com.nft.platform.dto.poe.request.LeaderboardRequestDto;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.dto.poe.request.UserBalanceRequestDto;
import com.nft.platform.dto.poe.response.LeaderboardResponseDto;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.service.poe.PoeTransactionService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Poe transactions Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions/poe")
public class PoeTransactionController {

    private final PoeTransactionService poeTransactionService;

    @PostMapping
    @Operation(summary = "Create Poe transaction")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public PoeTransactionResponseDto createPoeTransaction(
            @Parameter(name = "poeTransactionRequestDto", description = "Poe Transaction Request Dto")
            @Valid @RequestBody PoeTransactionRequestDto requestDto
    ) {
        return poeTransactionService.createPoeTransaction(requestDto);
    }

    @GetMapping("/user")
    @Operation(summary = "Get User Activity Balance")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({RoleConstants.ROLE_TECH_TOKEN})
    public Integer getUserBalance(@ParameterObject UserBalanceRequestDto requestDto) {
        return poeTransactionService.calculateUserActivityBalance(requestDto);
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get Leaderboard")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_USER})
    public LeaderboardResponseDto getLeaderboard(@ParameterObject LeaderboardRequestDto requestDto) {
        return poeTransactionService.calculateTopUsersActivityBalance(requestDto);
    }

}
