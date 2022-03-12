package com.nft.platform.dto.response;

import com.nft.platform.dto.AbstractProfileWalletDto;
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
@Schema(description = "ProfileWallet Response DTO")
public class ProfileWalletResponseDto extends AbstractProfileWalletDto {

    private CelebrityResponseDto celebrityDto;
}
