package com.nft.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
public class LeaderboardByIdDto {
    private Integer position;
    private Integer pointsBalance;
    private LeaderboardUserByIdDto user;
}
