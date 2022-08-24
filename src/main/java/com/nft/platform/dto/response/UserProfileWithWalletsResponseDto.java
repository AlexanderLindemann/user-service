package com.nft.platform.dto.response;

import com.nft.platform.common.dto.UserProfileResponseDto;
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
@Schema(description = "User Response With Profile Wallet and Crypto Wallet DTO")
public class UserProfileWithWalletsResponseDto extends UserProfileResponseDto {

    @Schema(description = "Profile Wallets List")
    List<ProfileWalletResponseDto> profileWalletDtos;

    @Schema(description = "Crypto Wallets List")
    List<CryptoWalletResponseDto> cryptoWalletDtos;

    @Schema(description = "Tmp Fan Token Balance (if cryptoWallets is empty)")
    BigDecimal tmpFanTokenBalance;
}
