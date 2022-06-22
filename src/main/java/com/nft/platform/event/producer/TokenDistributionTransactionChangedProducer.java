package com.nft.platform.event.producer;

import com.nft.platform.event.TokenDistributionTransactionChangedEvent;

public interface TokenDistributionTransactionChangedProducer {

    void handle(TokenDistributionTransactionChangedEvent event);
}
