package com.nft.platform.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TmpFanTokenDistributionEvent {

    private UUID keycloakUserId;
}
