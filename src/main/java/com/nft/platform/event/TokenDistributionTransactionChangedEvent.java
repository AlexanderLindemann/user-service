package com.nft.platform.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDistributionTransactionChangedEvent {

    // TODO remove duplicating with token-management-service
    private UUID transactionId;
    private String status;
}
