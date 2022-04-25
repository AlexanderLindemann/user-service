package com.nft.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Subscription Request DTO")
public class ProfileWalletVotesDto {

    @Schema(required = true)
    private int votes;

    @NotNull
    private UUID celebrityId;
}
