package com.nft.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Current User Response With Profile Wallet and Crypto Wallets DTO")
public class CurrentUserProfileWithWalletsResponseDto extends UserProfileResponseDto {

    @Schema(description = "Profile Wallet")
    ProfileWalletResponseDto profileWalletDto;

    @Schema(description = "Crypto Wallets List")
    List<CryptoWalletResponseDto> cryptoWalletDtos;

    @Schema(description = "Tmp Fan Token Balance (if cryptoWallets is empty)")
    BigDecimal tmpFanTokenBalance;
}
