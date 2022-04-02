package com.nft.platform.event;

import com.nft.platform.common.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProfileWalletCreatedEvent {

    @Schema(description = "keycloakId")
    private UUID userId;
    private UUID celebrityId;
    private UUID profileWalletId;
    private EventType eventType;
}
