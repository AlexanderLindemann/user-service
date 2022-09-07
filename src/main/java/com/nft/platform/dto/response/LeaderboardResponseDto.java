package com.nft.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nft.platform.dto.enums.LeaderboardGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardResponseDto {

    private List<LeaderboardPositionDto> firstBlock ;

    private List<LeaderboardPositionDto> secondBlock ;

    private List<LeaderboardPositionDto> thirdBlock;

    private LeaderboardGroup myCohort;

    private int myCohortRating;

    private int myCohortUsersCount;
}
