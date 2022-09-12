package com.nft.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nft.platform.dto.enums.LeaderboardGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class LeaderboardPositionDto {

    private LeaderboardGroup cohort;
    private int position;
    private int pointsBalance;
    private boolean currentUser;
    private LeaderboardUserDto userDto;

}
