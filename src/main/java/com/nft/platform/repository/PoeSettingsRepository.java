package com.nft.platform.repository;

import com.nft.platform.domain.PoeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PoeSettingsRepository extends JpaRepository<PoeSettings, UUID> {

    Optional<PoeSettings> findByName(String name);

}
