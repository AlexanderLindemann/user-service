package com.nft.platform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "celebrity")
public class Celebrity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "celebrity_signature")
    private String celebritySignature;

    @Column(name = "celebrity_video")
    private String celebrityVideo;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "android_link")
    private String androidLink;

    @Column(name = "ios_link")
    private String iosLink;

    @Column(name = "image_promo_url")
    private String imagePromoUrl;

    @ManyToMany
    @JoinTable(
            name = "celebrity_celebrity_category_map",
            joinColumns = {@JoinColumn(name = "id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    private List<CelebrityCategory> category;

//    @Type(type = "jsonb")
//    @Column(name = "theme", columnDefinition = "json")
//    private Object jsonTheme;
}
