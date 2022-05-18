package com.nft.platform.dto.response;

import com.nft.platform.dto.enums.BundleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BundleForCoinsResponseDto {

    private UUID id;

    private int bundleSize;
    private int coins;
    private int discount;

    private String imageUrl;

    private BundleType type;
}
