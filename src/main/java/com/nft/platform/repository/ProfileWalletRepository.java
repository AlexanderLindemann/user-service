package com.nft.platform.repository;

import com.nft.platform.domain.ProfileWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileWalletRepository extends JpaRepository<ProfileWallet, UUID> {
}
