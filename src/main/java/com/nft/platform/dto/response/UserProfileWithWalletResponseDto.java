package com.nft.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "User Response With Profile Wallet DTO")
public class UserProfileWithWalletResponseDto extends UserProfileResponseDto {

    @Schema(description = "Profile Wallet")
    private ProfileWalletResponseDto profileWalletDto;
}
