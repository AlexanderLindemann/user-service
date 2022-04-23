package com.nft.platform.domain.poe;

import com.nft.platform.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "poe_transaction")
public class PoeTransaction extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "celebrity_id", nullable = false)
    private UUID celebrityId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poe_id", nullable = false)
    private Poe poe;

    @Column(name = "points_reward")
    private Integer pointsReward;

    @Column(name = "coins_reward")
    private Integer coinsReward;
}
