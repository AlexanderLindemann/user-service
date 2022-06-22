package com.nft.platform.repository;

import com.nft.platform.domain.FanTokenDistributionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface FanTokenDistributionTransactionRepository extends JpaRepository<FanTokenDistributionTransaction, UUID> {

    int deleteByCreatedAtBefore(LocalDateTime before);

    @Query("select sum(tt.lamportsAmount) from FanTokenDistributionTransaction tt where tt.keycloakUserId = :keycloakUserId")
    long getFanTokenBalance(@Param("keycloakUserId") UUID keycloakUserId);
}
