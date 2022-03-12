package com.nft.platform.feign.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletBalanceResponse {

    private BigDecimal balance;
    private String pubkey;
}
