package com.nft.platform.service;

import com.nft.platform.domain.FanTokenDistributionTransaction;
import com.nft.platform.domain.RetryBlockchainTokenDistributionTransaction;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.event.FantokenTransactionCreatedEvent;
import com.nft.platform.event.TokenDistributionTransactionChangedEvent;
import com.nft.platform.event.producer.TokenDistributionTransactionChangedProducer;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.feign.client.SolanaAdapterClient;
import com.nft.platform.feign.client.dto.TransferFanTokenRequestDto;
import com.nft.platform.repository.FanTokenDistributionTransactionRepository;
import com.nft.platform.repository.RetryBlockchainTokenDistributionTransactionRepository;
import com.nft.platform.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FanTokenDistributionTransactionService {

    private final FanTokenDistributionTransactionRepository distributionTransactionRepository;
    private final UserProfileRepository userProfileRepository;
    private final SolanaAdapterClient solanaAdapterClient;
    private final RetryBlockchainTokenDistributionTransactionRepository retryBlockchainTokenDistributionTransactionRepository;
    private final TokenDistributionTransactionChangedProducer transactionChangedProducer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void distributeFanTokens(FantokenTransactionCreatedEvent event) {
        var keycloakUserId = event.getKeycloakUserId();
        var user = userProfileRepository.findByKeycloakIdWithCryptoWallets(keycloakUserId)
                .orElseThrow(() -> {
                    sendTransactionEvent(event, "error");
                    throw new ItemNotFoundException(UserProfile.class, keycloakUserId);
                });
        if (user.isHasCryptoWallets()) {
            // user has wallet
            try {
                var defaultWallet = user.getDefaultCryptoWallet().orElse(user.getCryptoWallets().iterator().next());
                sendTransferToChain(defaultWallet.getExternalCryptoWalletId(), event.getLamportsAmount());

                sendTransactionEvent(event, "success");
            } catch (Exception e) {
                var retryTransaction = RetryBlockchainTokenDistributionTransaction
                        .builder()
                        .transactionId(event.getTransactionId())
                        .keycloakUserId(keycloakUserId)
                        .periodId(event.getPeriodResponseDto().getId())
                        .lamportsAmount(event.getLamportsAmount())
                        .build();
                // TODO blockchain, now only SOLANA
                retryBlockchainTokenDistributionTransactionRepository.save(retryTransaction);
                sendTransactionEvent(event, "error");
            }
        } else {
            // user doesn't have wallet
            FanTokenDistributionTransaction fanTokenDistributionTransaction = FanTokenDistributionTransaction
                    .builder()
                    .transactionId(event.getTransactionId())
                    .keycloakUserId(event.getKeycloakUserId())
                    .periodId(event.getPeriodResponseDto().getId())
                    .lamportsAmount(event.getLamportsAmount())
                    .build();
            distributionTransactionRepository.save(fanTokenDistributionTransaction);
            sendTransactionEvent(event, "success");
        }
    }

    private void sendTransactionEvent(FantokenTransactionCreatedEvent event, String status) {
        TokenDistributionTransactionChangedEvent statusEvent = TokenDistributionTransactionChangedEvent.builder()
                .transactionId(event.getTransactionId())
                .status(status)
                .build();
        transactionChangedProducer.handle(statusEvent);
    }

    @Transactional
    public void sendTransferToChain(String cryptoWalletId, long amount) {
        log.info("sendTransferToChain to wallet = {} amount = {}", cryptoWalletId, amount);
        var body = TransferFanTokenRequestDto.builder()
                .wallet(cryptoWalletId)
                .amount(amount)
                .build();
        solanaAdapterClient.transferFanTokens(body);
    }

    @Transactional
    public void clearOlderThanDays(int days) {
        log.info("clearOlderThanDays started");
        LocalDateTime beforeThisDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(days);
        int deleted = distributionTransactionRepository.deleteByCreatedAtBefore(beforeThisDate);
        log.info("clearOlderThanDays finished, deleted = {}", deleted);
    }

    @Transactional(readOnly = true)
    public long getTmpFanTokenBalanceForUser(UserProfile userProfile) {
        log.debug("getFanTokenBalanceForUser started");
        var balance = distributionTransactionRepository.getFanTokenBalance(userProfile.getKeycloakUserId());
        log.debug("getFanTokenBalanceForUser finished, balance = {}", balance);
        return balance;
    }
}
