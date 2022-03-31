package com.nft.platform.dto.poe.response;

import com.nft.platform.dto.poe.AbstractPoeTransactionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Poe Transaction DTO")
public class PoeTransactionResponseDto extends AbstractPoeTransactionDto {

    @Schema(description = "Id", required = true)
    @NotNull
    private UUID id;

    @Schema(description = "Activity Reward")
    private Integer pointsReward;

    @Schema(description = "Coin Reward")
    private Integer coinsReward;
}
