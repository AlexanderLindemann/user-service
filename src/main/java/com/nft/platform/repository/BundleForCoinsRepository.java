package com.nft.platform.repository;

import com.nft.platform.domain.BundleForCoins;
import com.nft.platform.dto.enums.BundleType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BundleForCoinsRepository extends JpaRepository<BundleForCoins, UUID> {

    List<BundleForCoins> findByType(BundleType type, Sort sort);
}
