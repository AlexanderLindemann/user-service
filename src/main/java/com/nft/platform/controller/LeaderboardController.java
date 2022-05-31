package com.nft.platform.controller;

import com.nft.platform.dto.enums.LeaderboardGroup;
import com.nft.platform.dto.response.LeaderboardV2ResponseDto;
import com.nft.platform.service.LeaderboardService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Leaderboard Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Get Leaderboard V2")
    @ResponseStatus(HttpStatus.OK)
    public LeaderboardV2ResponseDto getLeaderboardV2() {
        return leaderboardService.calculateLeaderboard();
    }

    @GetMapping("/cohort")
    @Operation(summary = "Get Current User Cohort")
    @ResponseStatus(HttpStatus.OK)
    @Secured({RoleConstants.ROLE_USER})
    public LeaderboardGroup getCurrentUserCohort() {
        return leaderboardService.calculateCurrentUserCohort();
    }

}
