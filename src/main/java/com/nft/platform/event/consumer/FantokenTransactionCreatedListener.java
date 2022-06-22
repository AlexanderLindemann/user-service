package com.nft.platform.event.consumer;

import com.nft.platform.event.FantokenTransactionCreatedEvent;

public interface FantokenTransactionCreatedListener {

    void receive(FantokenTransactionCreatedEvent event);
}
