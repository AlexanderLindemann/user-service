package com.nft.platform.event.producer;

import com.nft.platform.common.event.TransactionEvent;

public interface IncomeHistoryTransactionEventProducer {
    void handle(TransactionEvent event);
}