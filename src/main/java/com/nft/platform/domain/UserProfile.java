package com.nft.platform.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    private UUID keycloakUserId;

    private String username;

    private String email;

    @Column(name = "is_invisible_email")
    private boolean invisibleEmail;

    private String imageUrl;

    private String phone;

    @Column(name = "is_verified_phone")
    private boolean verifiedPhone;

    @Column(name = "is_invisible_phone")
    private boolean invisiblePhone;

    @Column(name = "is_invisible_name")
    private boolean invisibleName;

    @Column(name = "is_two_factor_auth")
    private boolean twoFactoAuth;

    private String googleId;

    private String facebookId;

    private String twitterId;

    private String description;

    private String site;

    private String firstName;

    private String lastName;

    private String nickname;

    private String imagePromoBannerUrl;

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ProfileWallet> profileWallets;

    @OrderBy("createdAt asc")
    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CryptoWallet> cryptoWallets;

    @Transient
    public Optional<CryptoWallet> getDefaultCryptoWallet() {
        return cryptoWallets != null && !cryptoWallets.isEmpty()
                ? cryptoWallets.stream().filter(CryptoWallet::isDefaultWallet).findFirst()
                : Optional.empty();
    }

    @Transient
    public boolean isHasCryptoWallets() {
        return cryptoWallets != null && !cryptoWallets.isEmpty();
    }

}