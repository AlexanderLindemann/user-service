package com.nft.platform.dto.response;

import com.nft.platform.dto.AbstractCryptoWalletDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "CryptoWallet Response DTO")
public class CryptoWalletResponseDto extends AbstractCryptoWalletDto {

    @Schema(description = "CryptoWallet id")
    private UUID id;

    @Schema(description = "CryptoWallet Balance From Blockchain (if it is default)")
    private BigDecimal balance;
}
