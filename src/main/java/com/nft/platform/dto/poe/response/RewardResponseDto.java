package com.nft.platform.dto.poe.response;

import com.nft.platform.common.enums.PoeAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode()
@ToString(callSuper = true)
@Schema(description = "Reward Response DTO")
public class RewardResponseDto {
    @Schema(description = "Reward Id", required = true)
    private UUID id;

    @Schema(description = "Feed Id")
    private UUID actionId;

    @Schema(description = "Is Received Reward")
    private boolean isReceived;

    @Schema(description = "Reward Activity type")
    private PoeAction poeAction;

    @Schema(description = "Coins Reward")
    private int coins;

    @Schema(description = "Poe Reward")
    private int poe;
}
