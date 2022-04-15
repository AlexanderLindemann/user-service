package com.nft.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CelebrityShowcaseResponseDto {
    CelebrityNftResponseDto celebrity;
    NftResponseDto nft;
}
