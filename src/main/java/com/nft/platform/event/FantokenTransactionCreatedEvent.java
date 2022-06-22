package com.nft.platform.event;

import com.nft.platform.common.dto.PeriodResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FantokenTransactionCreatedEvent {

    // TODO remove dublicating with token-management-service
    private UUID transactionId;
    private UUID keycloakUserId;
    private PeriodResponseDto periodResponseDto;
    private long lamportsAmount;
}