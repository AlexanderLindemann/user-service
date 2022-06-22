package com.nft.platform.feign.client;

import com.nft.platform.feign.client.dto.TransferFanTokenRequestDto;
import com.nft.platform.feign.client.dto.WalletBalanceResponse;
import com.nft.platform.feign.client.dto.WalletFanTokenBalanceResponse;
import com.nft.platform.feign.client.dto.WalletInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = SolanaAdapterClient.NAME,
        url = "${feign." + SolanaAdapterClient.NAME + ".url}"
)
public interface SolanaAdapterClient {

    String NAME = "nft-solana-adapter";

    @GetMapping(value = "/api/solana/balance/{walletPubkey}")
    ResponseEntity<WalletBalanceResponse> getWalletBalance(@PathVariable("walletPubkey") String walletPubkey);

    @GetMapping(value = "/api/solana/token/balance/{walletPubkey}")
    ResponseEntity<WalletFanTokenBalanceResponse> getWalletFanBalance(@PathVariable("walletPubkey") String walletPubkey);

    @GetMapping(value = "/api/solana/info/{walletPubkey}")
    ResponseEntity<WalletInfoResponseDto> getWalletInfo(@PathVariable("walletPubkey") String walletPubkey);

    @PostMapping(value = "/api/solana/transver-fan-tokens")
    ResponseEntity<Void> transferFanTokens(@RequestBody TransferFanTokenRequestDto body);
}
