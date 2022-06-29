package com.nft.platform.repository;

import com.nft.platform.domain.Period;
import com.nft.platform.dto.enums.PeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PeriodRepository extends JpaRepository<Period, UUID> {

    Optional<Period> findByStatus(PeriodStatus status);

    @Query("SELECT p.endTime FROM Period p WHERE p.status = 'ACTIVE'")
    LocalDateTime findEndTimeByActiveStatus();

}