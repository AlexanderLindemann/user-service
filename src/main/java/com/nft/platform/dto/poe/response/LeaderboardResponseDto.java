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
@Schema(description = "Leaderboard Response DTO")
public class LeaderboardResponseDto {

    private List<UserActivityBalancePositionResponseDto> leaderboard;

    private UserActivityBalancePositionResponseDto currentUser;

    private long amountUsers;
}
