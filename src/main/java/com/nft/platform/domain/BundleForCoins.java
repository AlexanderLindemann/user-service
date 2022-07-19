package com.nft.platform.domain;

import com.nft.platform.common.enums.BundleType;

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
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "bundle_for_coins")
public class BundleForCoins extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "bundle_size", nullable = false)
    private int bundleSize;

    @Column(name = "coins", nullable = false)
    private int coins;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BundleType type;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
