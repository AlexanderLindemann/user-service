package com.nft.platform.repository;

import com.nft.platform.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID>, JpaSpecificationExecutor<UserProfile> {

    // many join fetch - it is not good for performance, but for one UserProfile it's ok
    @Query("select distinct up from UserProfile up join fetch up.profileWallets pw join fetch pw.celebrity join fetch up.cryptoWallets cw " +
            " where up.id = :id")
    Optional<UserProfile> findByIdWithWallets(UUID id);

    Optional<UserProfile> findByKeycloakUserId(UUID keycloakUserId);

    @Query("select distinct up from UserProfile up join fetch up.profileWallets pw join fetch pw.celebrity c " +
            " where up.keycloakUserId = :keycloakUserId and c.id = :celebrityId")
    Optional<UserProfile> findByKeycloakUserIdAndCelebrityId(UUID keycloakUserId, UUID celebrityId);

    @Query("select distinct up from UserProfile up join fetch up.profileWallets pw join fetch pw.celebrity where up.keycloakUserId = :keycloakUserId")
    Optional<UserProfile> findByKeycloakUserIdWithCelebrities(UUID keycloakUserId);
}
