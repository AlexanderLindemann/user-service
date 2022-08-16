package com.nft.platform.controller;

import com.nft.platform.dto.response.LeaderboardByIdResponseDto;
import com.nft.platform.dto.response.LeaderboardV2ResponseDto;
import com.nft.platform.exception.ExceptionDto;
import com.nft.platform.service.LeaderboardService;
import com.nft.platform.util.security.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public String getCurrentUserCohort() {
        return leaderboardService.calculateCurrentUserCohort().name();
    }


    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get Leaderboard by UUID", description = "Getting data from the leaderboard for the user and the current user by uuid", tags = {"leaderboard"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeaderboardByIdResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))})
    public LeaderboardByIdResponseDto getLeaderboardV2ById(@PathVariable UUID userId) {
        return leaderboardService.getLeaderboardByIdResponseDto(userId);
    }
}
