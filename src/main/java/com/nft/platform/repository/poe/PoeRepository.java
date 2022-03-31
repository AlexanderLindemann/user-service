package com.nft.platform.repository.poe;

import com.nft.platform.domain.poe.Poe;
import com.nft.platform.enums.PoeAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PoeRepository extends JpaRepository<Poe, UUID>, JpaSpecificationExecutor<Poe> {

    Optional<Poe> findByCode(PoeAction poeAction);
}
