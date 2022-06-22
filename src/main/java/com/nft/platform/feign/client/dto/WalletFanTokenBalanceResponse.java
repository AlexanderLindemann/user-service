package com.nft.platform.feign.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletFanTokenBalanceResponse {

    private BigDecimal tokenBalance;
    private String accountPubkey;
    private String tokenAccount;
}
