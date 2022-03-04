package com.nft.platform.repository;

import com.nft.platform.domain.CelebrityCategory;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CelebrityCategoryRepository extends JpaRepository<CelebrityCategory, UUID> {

    boolean existsByNameIgnoreCase(@NonNull String name);
}
