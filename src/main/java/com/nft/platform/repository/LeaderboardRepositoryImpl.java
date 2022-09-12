package com.nft.platform.repository;

import com.nft.platform.util.LeaderboardQueryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

import static com.nft.platform.util.LeaderboardQueryUtils.FIND_LEADERBOARD_ALL_USER;

@RequiredArgsConstructor
@Repository
public class LeaderboardRepositoryImpl implements LeaderboardRepository {


    private final EntityManager entityManager;

    @Override
    public List<Object[]> findAllLeaderboard() {
        return entityManager.createNativeQuery(FIND_LEADERBOARD_ALL_USER).getResultList();
    }

    @Override
    public List<Object[]> findLeaderboardByUserId(UUID userId) {
        return entityManager.createNativeQuery(LeaderboardQueryUtils.FIND_LEADERBOARD_BY_USER_ID)
                .setParameter("user_id", userId)
                .getResultList();
    }

    @Override
    public List<Object[]> findLeaderboardByKeycloakUserId(UUID keycloakUserid) {
        return entityManager.createNativeQuery(LeaderboardQueryUtils.FIND_LEADERBOARD_BY_KEYCLOAK_USER_ID)
                .setParameter("keycloak_user_id", keycloakUserid)
                .getResultList();
    }

    @Override
    public void refreshView() {
        entityManager.createNativeQuery(LeaderboardQueryUtils.REFRESH_QUERY).executeUpdate();
    }
}
