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
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "retry_blockchain_token_distribution_transaction")
public class RetryBlockchainTokenDistributionTransaction extends BaseEntity {

    @Id
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    @Column(name = "keycloak_user_id", nullable = false)
    private UUID keycloakUserId;

    @Column(name = "lamports_amount", nullable = false)
    private long lamportsAmount;

    // TODO now SOLANA is default blockchain
    @Enumerated(EnumType.STRING)
    @Column(name = "blockchain")
    private Blockchain blockchain = Blockchain.SOLANA;
}
