package com.nft.platform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "profile_wallet")
public class ProfileWallet extends BaseEntity {

    public ProfileWallet() {
        this.experienceCount = 0;
        this.activityBalance = BigDecimal.ZERO;
        this.coinBalance = BigDecimal.ZERO;
    }

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "experience_count", nullable = false)
    private int experienceCount;

    @Column(name = "vote_balance", nullable = false)
    private int voteBalance;

    @Column(name = "activity_balance", nullable = false)
    private BigDecimal activityBalance;

    @Column(name = "coin_balance", nullable = false)
    private BigDecimal coinBalance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "celebrity_id", nullable = false)
    private Celebrity celebrity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
}
