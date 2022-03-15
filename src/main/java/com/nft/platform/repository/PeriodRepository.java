package com.nft.platform.repository;

import com.nft.platform.domain.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PeriodRepository extends JpaRepository<Period, UUID> {

    Optional<Period> findFirst1ByOrderByStartTimeDesc();
}