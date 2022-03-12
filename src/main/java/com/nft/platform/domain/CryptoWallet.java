package com.nft.platform.domain;

import com.nft.platform.common.enums.Blockchain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "crypto_wallet")
public class CryptoWallet extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "external_crypto_wallet_id", nullable = false)
    private String externalCryptoWalletId;

    @Column(name = "default_wallet", nullable = false)
    private boolean defaultWallet = false;

    // TODO now SOLANA is default blockchain
    @Enumerated(EnumType.STRING)
    @Column(name = "blockchain")
    private Blockchain blockchain = Blockchain.SOLANA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
}
