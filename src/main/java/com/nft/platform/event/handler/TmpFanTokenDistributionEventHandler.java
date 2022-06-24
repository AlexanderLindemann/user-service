package com.nft.platform.event.handler;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.event.TmpFanTokenDistributionEvent;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.repository.UserProfileRepository;
import com.nft.platform.service.FanTokenDistributionTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Optional;

@EnableAsync
@Slf4j
@RequiredArgsConstructor
@Component
public class TmpFanTokenDistributionEventHandler {

    private final FanTokenDistributionTransactionService distributionTransactionService;
    private final UserProfileRepository userProfileRepository;

    @Async
    @EventListener
    public void handle(TmpFanTokenDistributionEvent event) {
        log.info("handle event = {}", event);
        var keycloakUserId = event.getKeycloakUserId();
        UserProfile userProfile = userProfileRepository.findByKeycloakIdWithCryptoWallets(keycloakUserId)
                .orElseThrow(() -> new ItemNotFoundException(UserProfile.class, keycloakUserId));
        Optional<Long> amount = distributionTransactionService.getTmpFanTokenBalanceForUser(userProfile);
        String defaultWallet = userProfile.getDefaultCryptoWallet().map(CryptoWallet::getExternalCryptoWalletId).orElse(null);
        if (!StringUtils.isEmpty(defaultWallet)) {
            log.info("sending amount = {} to wallet = {}", amount.orElse(0L), defaultWallet);
            distributionTransactionService.sendTransferToChain(defaultWallet, amount.orElse(0L));
        }
        log.info("handle TmpFanTokenDistributionEvent finished");
    }
}
