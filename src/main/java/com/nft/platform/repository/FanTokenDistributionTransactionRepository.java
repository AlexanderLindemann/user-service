package com.nft.platform.repository;

import com.nft.platform.domain.FanTokenDistributionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FanTokenDistributionTransactionRepository extends JpaRepository<FanTokenDistributionTransaction, UUID> {

    int deleteByCreatedAtBefore(LocalDateTime before);

    @Query("SELECT SUM(tt.lamportsAmount) FROM FanTokenDistributionTransaction tt WHERE tt.keycloakUserId = :keycloakUserId")
    Long getFanTokenBalance(UUID keycloakUserId);

}
