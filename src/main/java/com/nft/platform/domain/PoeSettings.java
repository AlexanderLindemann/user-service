package com.nft.platform.domain;

import com.nft.platform.enums.PoeRewardType;
import com.nft.platform.enums.PoeTimeType;
import com.nft.platform.enums.PoeType;
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
import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "poe_settings")
public class PoeSettings extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "group")
    private String group;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private PoeType type;

    @Column(name = "comment")
    private String comment;

    @Column(name = "activity_reward_type")
    @Enumerated(EnumType.STRING)
    private PoeRewardType activityRewardType;

    @Column(name = "activity_reward_fix")
    private Integer activityRewardFix;

    @Column(name = "activity_reward_formula")
    private String activityRewardFormula;

    @Column(name = "coin_reward_type")
    @Enumerated(EnumType.STRING)
    private PoeRewardType coinRewardType;

    @Column(name = "coin_reward_fix")
    private Integer coinRewardFix;

    @Column(name = "coin_reward_formula")
    private String coinRewardFormula;

    @Column(name = "activity_reward_to_sub_type")
    @Enumerated(EnumType.STRING)
    private PoeRewardType activityRewardToSubType;

    @Column(name = "activity_reward_to_sub_fix")
    private Integer activityRewardToSubFix;

    @Column(name = "activity_reward_to_sub_formula")
    private String activityRewardToSubFormula;

    @Column(name = "coin_reward_to_sub_type")
    @Enumerated(EnumType.STRING)
    private PoeRewardType coinRewardToSubType;

    @Column(name = "coin_reward_to_sub_fix")
    private Integer coinRewardToSubFix;

    @Column(name = "coin_reward_to_sub_formula")
    private String coinRewardToSubFormula;

    @Column(name = "price_in_usd")
    private BigDecimal priceInUsd;

    @Column(name = "price_in_coins")
    private BigDecimal priceInCoins;

    @Column(name = "free_amount_on_period_type")
    @Enumerated(EnumType.STRING)
    private PoeTimeType freeAmountOnPeriodType;

    @Column(name = "free_amount_on_period", nullable = false)
    private Integer freeAmountOnPeriod;

    @Column(name = "free_amount_on_period_sub_type")
    @Enumerated(EnumType.STRING)
    private PoeTimeType freeAmountOnPeriodSubType;

    @Column(name = "free_amount_on_period_sub", nullable = false)
    private Integer freeAmountOnPeriodSub;

    @Column(name = "free_amount_on_onboarding")
    private Integer freeAmountOnOnboarding;
}
