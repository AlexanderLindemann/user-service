package com.nft.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Schema(description = "Profile Wallet Period Update Dto")
public class ProfileWalletPeriodUpdateDto {

    private UUID celebrityId;
}
