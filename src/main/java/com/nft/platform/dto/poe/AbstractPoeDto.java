package com.nft.platform.dto.poe;

import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.common.enums.PoeGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPoeDto {

    @NotNull
    @Schema(required = true)
    private PoeAction code;

    @NotBlank
    @Schema(required = true)
    private String name;

    @NotNull
    @Schema(required = true)
    private PoeGroup group;

    private String comment;
    private Integer pointsReward;
    private Integer coinsReward;
    private Integer coinsRewardSub;
    private Integer pointsRewardSub;
    private Integer freeAmountOnPeriod;
    private Integer freeAmountOnPeriodSub;

    private BigDecimal usdPrice;
    private BigDecimal coinsPrice;
}
