package com.nft.platform.dto.poe.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Balance Response DTO")
public class UserActivityBalancePositionResponseDto {

    @Schema(description = "User Dto", required = true)
    private UserLeaderboardResponseDto userLeaderboardResponseDto;

    @Schema(description = "Activity Balance")
    private Integer activityBalance;

    @Schema(description = "Position")
    private Integer position;
}
