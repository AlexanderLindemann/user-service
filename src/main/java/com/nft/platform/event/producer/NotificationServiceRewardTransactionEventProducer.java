package com.nft.platform.event.producer;

import com.nft.platform.common.event.NotificationRewardEvent;

public interface NotificationServiceRewardTransactionEventProducer {
    void handle(NotificationRewardEvent event);
}
