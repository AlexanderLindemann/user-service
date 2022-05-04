package com.nft.platform.repository;

import com.nft.platform.domain.VotePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VotePriceRepository extends JpaRepository<VotePrice, UUID> {

    Optional<VotePrice> findByVotes(int votes);
}
