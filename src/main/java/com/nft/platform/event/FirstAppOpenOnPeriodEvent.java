package com.nft.platform.event;

import com.nft.platform.common.enums.EventType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FirstAppOpenOnPeriodEvent {

    private UUID userId;
    private UUID celebrityId;
    private EventType eventType;
}
