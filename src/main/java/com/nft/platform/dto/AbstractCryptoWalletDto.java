package com.nft.platform.dto;

import com.nft.platform.common.enums.Blockchain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

@SuperBuilder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractCryptoWalletDto {

    @Schema(description = "externalCryptoWalletId")
    @Size(max = 1024)
    private String externalCryptoWalletId;

    @Schema(description = "defaultWallet")
    private boolean defaultWallet;

    @Schema(description = "blockchain")
    private Blockchain blockchain;
}
