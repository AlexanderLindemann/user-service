package com.nft.platform.repository;

import com.nft.platform.domain.RetryBlockchainTokenDistributionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface RetryBlockchainTokenDistributionTransactionRepository extends JpaRepository<RetryBlockchainTokenDistributionTransaction, UUID> {

    int deleteByCreatedAtBefore(LocalDateTime before);
}
