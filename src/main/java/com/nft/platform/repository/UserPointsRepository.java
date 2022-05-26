package com.nft.platform.repository;

import com.nft.platform.domain.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserPointsRepository extends JpaRepository<UserPoints, UUID>, JpaSpecificationExecutor<UserPoints> {

    boolean existsByPeriodIdAndKeycloakUserId(UUID periodId, UUID keycloakUserId);

    @Modifying
    @Query(
            value = ""
                    + "UPDATE user_points "
                    + "SET points = points + :points "
                    + "WHERE period_id = :periodId AND keycloak_user_id = :keycloakUserId",
            nativeQuery = true
    )
    void updateUserPoints(UUID periodId, UUID keycloakUserId, int points);
}
