package com.nft.platform.dto.request;

import com.nft.platform.dto.AbstractCryptoWalletDto;
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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Crypto Wallet Request DTO")
public class CryptoWalletRequestDto extends AbstractCryptoWalletDto {

    @Schema(description = "User Profile Id")
    private UUID userProfileId;

    @Schema(description = "User Keycloak Id")
    private UUID userKeycloakId;
}
