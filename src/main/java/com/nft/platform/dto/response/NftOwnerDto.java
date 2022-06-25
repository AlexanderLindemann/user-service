package com.nft.platform.dto.response;

import com.nft.platform.enums.OwnerType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftOwnerDto {
    private UUID id;
    private String name;
    private OwnerType type;
    private List<String> avatars;
}