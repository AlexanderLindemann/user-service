package com.nft.platform.repository;

import com.nft.platform.common.dto.ContentAuthorDto;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.response.LeaderboardUserByIdDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID>, JpaSpecificationExecutor<UserProfile> {

    // many join fetch - it is not good for performance, but for one UserProfile it's ok
    @Query("select distinct up from UserProfile up left join fetch up.profileWallets pw left join fetch pw.celebrity left join fetch up.cryptoWallets cw " +
            " where up.id = :id")
    Optional<UserProfile> findByIdWithWallets(UUID id);

    // many join fetch - it is not good for performance, but for one UserProfile it's ok
    @Query("select distinct up from UserProfile up left join fetch up.profileWallets pw left join fetch pw.celebrity c left join fetch up.cryptoWallets cw " +
            " where up.keycloakUserId = :keycloakUserId and c.id = :celebrityId")
    Optional<UserProfile> findByKeycloakIdAndCelebrityIdWithWallets(UUID keycloakUserId, UUID celebrityId);

    @Query("select distinct up from UserProfile up left join fetch up.cryptoWallets cw " +
            " where up.keycloakUserId = :keycloakUserId ")
    Optional<UserProfile> findByKeycloakIdWithCryptoWallets(UUID keycloakUserId);

    Optional<UserProfile> findByKeycloakUserId(UUID keycloakUserId);

    @Query("select new com.nft.platform.dto.response.LeaderboardUserByIdDto(u.keycloakUserId, u.username, u.imageUrl,u.invisibleName) from UserProfile u where u.keycloakUserId = ?1")
    Optional<LeaderboardUserByIdDto> findLeaderboardUserByIdDtoByKeycloakUserId(UUID keycloakUserId);


    List<UserProfile> findByKeycloakUserIdIn(Collection<UUID> keycloakUserIds);

    @Query("select distinct up from UserProfile up join fetch up.profileWallets pw join fetch pw.celebrity c " +
            " where up.keycloakUserId = :keycloakUserId and c.id = :celebrityId")
    Optional<UserProfile> findByKeycloakUserIdAndCelebrityId(UUID keycloakUserId, UUID celebrityId);

    @Query("select distinct up from UserProfile up left join fetch up.profileWallets pw left join fetch pw.celebrity where up.keycloakUserId = :keycloakUserId")
    Optional<UserProfile> findByKeycloakUserIdWithCelebrities(UUID keycloakUserId);

    @Query("select up from UserProfile up where up.id in :userIds")
    List<UserProfile> findAllByIds(List<UUID> userIds);

    @Query("SELECT DISTINCT up.imageUrl FROM UserProfile up WHERE up.keycloakUserId IN (:userIds)")
    Set<String> findImageIdsByUserIds(List<UUID> userIds);

    @Query("SELECT up FROM UserProfile up WHERE (up.username = :name AND :name IS NOT NULL ) " +
            "OR (up.email = :email AND :email IS NOT NULL) OR (up.phone = :phone AND :phone IS NOT NULL)")
    Collection<UserProfile> findUserProfileBy(String name, String email, String phone);

    @Query("SELECT new com.nft.platform.common.dto.ContentAuthorDto(up.keycloakUserId, up.firstName, up.lastName, up.nickname, " +
            "up.imageUrl, up.invisibleName) FROM UserProfile up " +
            "WHERE up.keycloakUserId in ?1")
    Stream<ContentAuthorDto> findContentAuthorsByKeycloakIdIn(Collection<UUID> authorKeycloakIds);
}