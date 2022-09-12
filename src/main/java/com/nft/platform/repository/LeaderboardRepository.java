package com.nft.platform.repository;

import java.util.List;
import java.util.UUID;

public interface LeaderboardRepository {

    List<Object[]> findAllLeaderboard();

    List<Object[]> findLeaderboardByUserId(UUID userId);

    List<Object[]> findLeaderboardByKeycloakUserId(UUID keycloakUserid);

    void refreshView();
}
