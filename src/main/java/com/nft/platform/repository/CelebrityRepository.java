package com.nft.platform.repository;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.view.CelebrityView;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface CelebrityRepository extends JpaRepository<Celebrity, UUID>, JpaSpecificationExecutor<Celebrity> {

    Optional<Celebrity> findByIdAndActiveTrue(UUID id);

    boolean existsByNameIgnoreCase(@NonNull String name);

    Optional<Celebrity> findById(UUID id);

    List<Celebrity> findAllByActiveIsTrue();

    Stream<CelebrityView> findByIdIn(Set<UUID> celebrityIds);

    @Query(value = "SELECT cb FROM Celebrity cb WHERE cb.id <> :techCelebrityId AND cb.id NOT IN (" +
            "SELECT pw.celebrity.id FROM ProfileWallet pw WHERE pw.userProfile.keycloakUserId = :keycloakUserId)")
    Page<Celebrity> findAllUnsubscribedCelebrities(UUID keycloakUserId, Pageable pageable, UUID techCelebrityId);

    @Query(value = "SELECT cb FROM Celebrity cb WHERE cb.id <> :techCelebrityId AND cb.id IN (" +
            "SELECT pw.celebrity.id FROM ProfileWallet pw WHERE pw.userProfile.keycloakUserId = :keycloakUserId)")
    List<Celebrity> findAllSubscribedCelebrities(UUID keycloakUserId, UUID techCelebrityId);

    boolean existsByIdAndActiveTrue(UUID id);
}