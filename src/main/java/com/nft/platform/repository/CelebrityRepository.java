package com.nft.platform.repository;

import com.nft.platform.domain.Celebrity;

import com.nft.platform.domain.view.CelebrityView;
import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface CelebrityRepository extends JpaRepository<Celebrity, UUID> {
    Optional<Celebrity> findByName(@NonNull String name);

    Optional<Celebrity> findByIdAndActiveTrue(UUID id);

    Page<Celebrity> findAllByActiveTrue(Pageable pageable);

    boolean existsByNameIgnoreCase(@NonNull String name);

    List<Celebrity> findCelebritiesByNameContainsIgnoreCaseAndActiveTrue(@NonNull String searchName);

    Optional<Celebrity> findById(UUID id);

    List<Celebrity> findAllByActiveIsTrue();

    Stream<CelebrityView> findByIdIn(Set<UUID> celebrityIds);

}