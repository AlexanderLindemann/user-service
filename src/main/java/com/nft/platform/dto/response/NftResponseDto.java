package com.nft.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.UUID;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NftResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String file;
    private String arweaveFile;
    private Double priceToken;
    private Double priceUsd;
    private Double priceSol;
    private LocalDateTime endTime;
    private boolean my;
    private String sellType;
    private boolean onSale;
    private boolean hasMyBid;
    private UUID celebrityId;
    private String celebrityName;
}
