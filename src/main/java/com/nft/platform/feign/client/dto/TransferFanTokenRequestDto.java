package com.nft.platform.feign.client.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransferFanTokenRequestDto {
    private String wallet;
    private long amount;
}
