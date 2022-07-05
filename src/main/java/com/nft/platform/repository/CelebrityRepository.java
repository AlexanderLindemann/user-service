package com.nft.platform.repository;

import com.nft.platform.domain.Celebrity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CelebrityRepository extends JpaRepository<Celebrity, UUID> {

    Optional<Celebrity> findByName(@NonNull String name);

    boolean existsByNameIgnoreCase(@NonNull String name);

    List<Celebrity> findCelebritiesByNameContainsIgnoreCase(String searchName);

}