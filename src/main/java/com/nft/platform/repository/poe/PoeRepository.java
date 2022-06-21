package com.nft.platform.repository.poe;

import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.domain.poe.Poe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PoeRepository extends JpaRepository<Poe, UUID>, JpaSpecificationExecutor<Poe> {

    Optional<Poe> findByCode(PoeAction poeAction);

    List<Poe> findAll();
}
