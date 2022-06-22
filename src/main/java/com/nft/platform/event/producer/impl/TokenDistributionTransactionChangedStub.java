package com.nft.platform.event.producer.impl;

import com.nft.platform.event.TokenDistributionTransactionChangedEvent;
import com.nft.platform.event.producer.TokenDistributionTransactionChangedProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "spring.kafka.producer", name = "enabled", havingValue = "false", matchIfMissing = true)
@Component
@Slf4j
public class TokenDistributionTransactionChangedStub implements TokenDistributionTransactionChangedProducer {

    @Override
    @EventListener
    public void handle(TokenDistributionTransactionChangedEvent event) {
        log.info("Do nothing");
    }
}
