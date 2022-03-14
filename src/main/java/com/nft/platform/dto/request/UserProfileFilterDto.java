package com.nft.platform.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileFilterDto {

    Set<UUID> keycloakUserIds;
}
