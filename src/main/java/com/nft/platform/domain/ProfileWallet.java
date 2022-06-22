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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "profile_wallet")
@NamedQueries({
        @NamedQuery(name = "ProfileWallet.findAllCelebrityFans", query = "SELECT up " +
                "            FROM ProfileWallet up " +
                "            JOIN FETCH up.celebrity " +
                "            JOIN FETCH up.userProfile " +
                "            WHERE up.subscriber = true " +
                "            AND up.celebrity.id = :celebrityId")
})
public class ProfileWallet extends BaseEntity {

    public ProfileWallet() {
        this.subscriber = false;
        this.experienceCount = 0;
        this.coinBalance = 0L;
    }

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "experience_count", nullable = false)
    private int experienceCount;

    @Column(name = "vote_balance", nullable = false)
    private int voteBalance;

    @Column(name = "wheel_balance", nullable = false)
    private int wheelBalance;

    @Column(name = "coin_balance", nullable = false)
    private long coinBalance;

    @Column(name = "subscriber", nullable = false)
    private boolean subscriber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "celebrity_id", nullable = false)
    private Celebrity celebrity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id")
    private Period period;
}
