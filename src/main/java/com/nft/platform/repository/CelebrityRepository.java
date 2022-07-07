package com.nft.platform.repository;

import com.nft.platform.domain.Celebrity;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CelebrityRepository extends JpaRepository<Celebrity, UUID> {
    Optional<Celebrity> findByName(@NonNull String name);

    Optional<Celebrity> findByIdAndActiveTrue(UUID id);

    Page<Celebrity> findAllByActiveTrue(Pageable pageable);

    boolean existsByNameIgnoreCase(@NonNull String name);

    List<Celebrity> findCelebritiesByNameContainsAndActiveTrueIgnoreCase(String searchName);

    Optional<Celebrity> findById(UUID id);

}