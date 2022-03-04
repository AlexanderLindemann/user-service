package com.nft.platform.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "celebrity_category")
public class CelebrityCategory extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private UUID id;

    @Column(name = "category_name")
    private String name;

    @Column(name = "category_img")
    private String imageUrl;

    @Column(name = "code")
    private String code;
}
