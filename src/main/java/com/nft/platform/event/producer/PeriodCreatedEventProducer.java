package com.nft.platform.event.producer;

import com.nft.platform.common.event.PeriodCreatedEvent;

public interface PeriodCreatedEventProducer {

    void handle(PeriodCreatedEvent event);
}
