package com.nft.platform.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
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

    @Type(type = "jsonb")
    @Column(name = "theme", columnDefinition = "json")
    private Object jsonTheme;

}