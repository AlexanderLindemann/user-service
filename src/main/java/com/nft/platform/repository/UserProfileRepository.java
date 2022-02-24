package com.nft.platform.repository;

import com.nft.platform.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByKeycloakUserId(UUID keycloakUserId);

    @Query("select distinct up from UserProfile up join fetch up.profileWallets pw join fetch pw.celebrity")
    Optional<UserProfile> findByKeycloakUserIdWithCelebrities(UUID keycloakUserId);
}
