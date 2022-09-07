package com.nft.platform.controller;

import com.nft.platform.dto.enums.LeaderboardGroup;
import com.nft.platform.dto.response.LeaderboardByIdResponseDto;
import com.nft.platform.dto.response.LeaderboardResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Leaderboard")
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all leaderboard", description = "Getting a leaderboard and distributing users by cohorts", tags = {"leaderboard"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeaderboardResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))})
    public ResponseEntity<LeaderboardResponseDto> getLeaderboard() {
        LeaderboardResponseDto leaderboardV2ResponseDtoNew = leaderboardService.getLeaderboardResponseDto();
        return ResponseEntity.ok(leaderboardV2ResponseDtoNew);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get leaderboard by userId", description = "Getting data from the leaderboard for the user and the current user by userId", tags = {"leaderboard"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeaderboardByIdResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))})
    public ResponseEntity<LeaderboardByIdResponseDto> getLeaderboardV2ById(@PathVariable UUID userId) {
        return ResponseEntity.ok(leaderboardService.getLeaderboardByIdResponseDto(userId));
    }


    @GetMapping("/cohort")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get current user cohort", description = "Getting a current user and distributing users by cohort", tags = {"leaderboard"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeaderboardGroup.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))})
    @Secured({RoleConstants.ROLE_USER})
    public ResponseEntity<String> getCurrentUserCohort() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(leaderboardService.calculateCurrentUserCohort());
    }
}
