package com.nft.platform.domain.poe;

import com.nft.platform.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "action_id")
    private UUID actionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poe_id", nullable = false)
    private Poe poe;

    @Column(name = "points_reward")
    private Integer pointsReward;

    @Column(name = "coins_reward")
    private Integer coinsReward;

}