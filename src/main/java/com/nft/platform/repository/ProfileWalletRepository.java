package com.nft.platform.repository;

import com.nft.platform.domain.ProfileWallet;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileWalletRepository extends JpaRepository<ProfileWallet, UUID> {

    Optional<ProfileWallet> findByUserProfileIdAndCelebrityId(@NonNull UUID userId, @NonNull UUID celebrityId);

    @Query(value = "select pW " +
            "from ProfileWallet pW " +
            "where pW.userProfile.keycloakUserId = :keycloakUserId " +
            "and pW.celebrity.id = :celebrityId")
    Optional<ProfileWallet> findByKeycloakUserIdAndCelebrityId(@NonNull UUID keycloakUserId, @NonNull UUID celebrityId);

    @Query(value = "SELECT pw.subscriber " +
            "FROM ProfileWallet pw " +
            "WHERE pw.userProfile.keycloakUserId = :keycloakUserId AND pw.celebrity.id = :celebrityId")
    Boolean findIfUserSubscriberByKeycloakUserIdAndCelebrityId(@NonNull UUID keycloakUserId, @NonNull UUID celebrityId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT pW " +
            "FROM ProfileWallet pW " +
            "WHERE pW.userProfile.keycloakUserId = :keycloakUserId " +
            "AND pW.celebrity.id = :celebrityId"
    )
    Optional<ProfileWallet> findByKeycloakUserIdAndCelebrityIdForUpdate(@NonNull UUID keycloakUserId, @NonNull UUID celebrityId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET coin_balance = coin_balance + :coins"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    int updateProfileWalletCoinBalance(UUID keycloakUserId, UUID celebrityId, int coins);

    @Modifying
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET wheel_balance = wheel_balance + :amount"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    int updateProfileWalletWheelBalance(UUID keycloakUserId, UUID celebrityId, int amount);

    @Modifying
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET vote_balance = vote_balance + :amount"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    int updateProfileWalletVoteBalance(UUID keycloakUserId, UUID celebrityId, int amount);

    @Modifying
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET nft_votes_balance = nft_votes_balance + :amount"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    int updateProfileWalletNftVoteBalance(UUID keycloakUserId, UUID celebrityId, int amount);

    @Modifying
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET subscriber = :subscribed"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    int updateProfileWalletSubscription(UUID keycloakUserId, UUID celebrityId, boolean subscribed);

    @Modifying
    @Query(value = "UPDATE profile_wallet AS pw"
            + " SET experience_count = experience_count + :experience"
            + " FROM user_profile AS up"
            + " WHERE up.keycloak_user_id = :keycloakUserId"
            + " AND pw.celebrity_id = :celebrityId"
            + " AND pw.user_profile_id = up.id",
            nativeQuery = true)
    void updateProfileWalletExperienceBalance(UUID keycloakUserId, UUID celebrityId, int experience);

    List<ProfileWallet> findAllByUserProfileId(UUID userId);

}