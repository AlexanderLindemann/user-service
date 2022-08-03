package com.nft.platform.domain.poe;

import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.common.enums.PoeGroup;
import com.nft.platform.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "poe")
public class Poe extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private PoeAction code;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_name", nullable = false)
    private PoeGroup group;

    @Size(max = 1024)
    @Column(name = "comment")
    private String comment;

    @Column(name = "points_reward")
    private Integer pointsReward;

    @Column(name = "coins_reward")
    private Integer coinsReward;

    @Column(name = "coins_reward_sub")
    private Integer coinsRewardSub;

    @Column(name = "points_reward_sub")
    private Integer pointsRewardSub;

    @DecimalMin(value = "0.0")
    @Column(name = "usd_price")
    private BigDecimal usdPrice;

    @DecimalMin(value = "0.0")
    @Column(name = "coins_price")
    private BigDecimal coinsPrice;

    @Column(name = "free_amount_on_period")
    private Integer freeAmountOnPeriod;

    @Column(name = "free_amount_on_period_sub")
    private Integer freeAmountOnPeriodSub;
}
