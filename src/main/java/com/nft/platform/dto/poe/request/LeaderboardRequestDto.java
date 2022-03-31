package com.nft.platform.dto.poe.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Top Users Balance Request DTO")
public class LeaderboardRequestDto {

    @Schema(description = "Celebrity Id", required = true)
    private UUID celebrityId;

    @Schema(description = "Period Id")
    private UUID periodId;

    @Schema(description = "Leaderboard from position include")
    @Builder.Default
    private Integer from = 1;

    @Schema(description = "Leaderboard to position include")
    @Builder.Default
    private Integer to = 10;
}
