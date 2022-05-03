package com.nft.platform.dto.poe.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Leaderboard Full Response DTO")
public class LeaderboardFullResponseDto {
    private List<UserIdActivityBalancePositionResponseDto> leaderboard;
}

