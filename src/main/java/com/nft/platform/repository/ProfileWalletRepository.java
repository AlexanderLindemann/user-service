package com.nft.platform.repository;

import com.nft.platform.domain.ProfileWallet;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileWalletRepository extends JpaRepository<ProfileWallet, UUID> {

    Optional<ProfileWallet> findByUserProfileIdAndCelebrityId(@NonNull UUID userId, @NonNull UUID celebrityId);

    boolean existsByUserProfileIdAndCelebrityId(@NonNull UUID userId, @NonNull UUID celebrityId);

    @Query(value = "select pW " +
            "from ProfileWallet pW " +
            "where pW.userProfile.keycloakUserId = :keycloakUserId " +
            "and pW.celebrity.id = :celebrityId")
    Optional<ProfileWallet> findByKeycloakUserIdAndCelebrityId(@NonNull UUID keycloakUserId, @NonNull UUID celebrityId);

    @Query(value = "select voteBalance"
            + " from ProfileWallet pW"
            + " where pW.userProfile.keycloakUserId = :keycloakUserId"
            + " and pW.celebrity.id = :celebrityId")
    Optional<Integer> findVoteBalance(
            @Param("keycloakUserId") UUID keycloakUserId,
            @Param("celebrityId") UUID celebrityId
    );

    @Modifying
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET vote_balance = vote_balance - 1"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    int decrementUserVotes(@Param("keycloakUserId") UUID keycloakUserId, @Param("celebrityId") UUID celebrityId);

    @Modifying
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET coin_balance = coin_balance + :coins"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    int updateProfileWalletBalance(UUID keycloakUserId, UUID celebrityId, int coins);
}
