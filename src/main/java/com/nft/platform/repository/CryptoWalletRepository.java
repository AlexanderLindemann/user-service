package com.nft.platform.repository;

import com.nft.platform.common.enums.Blockchain;
import com.nft.platform.domain.CryptoWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWallet, UUID> {

    List<CryptoWallet> findAllByUserProfileIdOrderByUpdatedAt(@NotNull UUID userProfileId);

    List<CryptoWallet> findAllByUserProfileKeycloakUserIdOrderByUpdatedAt(@NotNull UUID userKeycloakId);

    boolean existsByExternalCryptoWalletIdAndBlockchain(@NotNull String cryptoWalletId, @NotNull Blockchain blockchain);

    Optional<CryptoWallet> findByExternalCryptoWalletIdAndBlockchain(@NotNull String cryptoWalletId, @NotNull Blockchain blockchain);

    @Modifying
    @Query("update CryptoWallet cw set cw.defaultWallet = :isDefault where cw.id in (:cwIds)")
    int setCryptoWalletsDefaultByIds(@Param("isDefault") boolean isDefault, @Param("cwIds") List<UUID> cwIds);


}
