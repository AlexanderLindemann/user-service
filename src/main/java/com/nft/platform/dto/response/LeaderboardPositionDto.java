package com.nft.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nft.platform.dto.enums.LeaderboardGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardPositionDto {

    private LeaderboardGroup cohort;
    private long position;
    private int pointsBalance;
    private boolean currentUser;
    private LeaderboardUserDto userDto;
}
