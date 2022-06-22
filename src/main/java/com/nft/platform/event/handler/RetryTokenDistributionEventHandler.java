package com.nft.platform.event.handler;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.event.RetryTokenDistributionEvent;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.repository.RetryBlockchainTokenDistributionTransactionRepository;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.service.FanTokenDistributionTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@EnableAsync
@Slf4j
@RequiredArgsConstructor
@Component
public class RetryTokenDistributionEventHandler {

    private final FanTokenDistributionTransactionService distributionTransactionService;
    private final UserProfileRepository userProfileRepository;
    private final RetryBlockchainTokenDistributionTransactionRepository retryTransactionRepository;

    @Async
    @EventListener
    public void handle(RetryTokenDistributionEvent event) {
        log.info("handle event = {}", event);
        var keycloakUserId = event.getKeycloakUserId();
        UserProfile userProfile = userProfileRepository.findByKeycloakIdWithCryptoWallets(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
        String defaultWallet = userProfile.getDefaultCryptoWallet().map(CryptoWallet::getExternalCryptoWalletId).orElse(null);
        if (!StringUtils.isEmpty(defaultWallet)) {
            distributionTransactionService.sendTransferToChain(defaultWallet, event.getAmount());
            retryTransactionRepository.deleteById(event.getTransactionId());
        }
    }
}
