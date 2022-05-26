package com.nft.platform.domain;

import com.nft.platform.dto.enums.LeaderboardGroup;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LeaderboardRow {

    private int rowNumber;
    private UUID keycloakUserId;
    private int points;
    private LeaderboardGroup userGroup;
}
