package com.nft.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nft.platform.dto.enums.LeaderboardGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardResponseDto {

    @Builder.Default
    private List<LeaderboardPositionDto> firstBlock = new ArrayList<>();

    @Builder.Default
    private List<LeaderboardPositionDto> secondBlock = new ArrayList<>();

    @Builder.Default
    private List<LeaderboardPositionDto> thirdBlock = new ArrayList<>();

    private LeaderboardGroup myCohort;

    private int myCohortRating;

    private int myCohortUsersCount;
}
