package com.nft.platform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@Entity
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
    private UUID id;
    private int experienceCount;
    private int voteBalance;
    private int wheelBalance;
    private long coinBalance;
    private int nftVotesBalance;
    private boolean subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celebrity_id", nullable = false)
    private Celebrity celebrity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id")
    private Period period;
}
