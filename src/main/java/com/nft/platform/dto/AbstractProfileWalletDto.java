package com.nft.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractProfileWalletDto {

    @Schema(description = "Leveling")
    private int experienceCount;

    @Schema(description = "POE")
    private long activityBalance;

    @Schema(description = "Coins")
    private long coinBalance;

    @Schema(description = "Vote balance")
    private int voteBalance;

    @Schema(description = "Wheel balance")
    private int wheelBalance;

    @Schema(description = "Nft Votes balance")
    private int nftVotesBalance;
}
