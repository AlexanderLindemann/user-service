package com.nft.platform.feign.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletInfoResponseDto {
    private boolean executable;
    private BigDecimal lamports;
    private String owner;
    private BigDecimal rentEpoch;
}
