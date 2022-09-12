package com.nft.platform.domain;


import com.nft.platform.dto.enums.LeaderboardGroup;
import com.nft.platform.dto.response.LeaderboardUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardDto {

    private int rowNumber;

    private UUID keycloakUserId;

    private int points;

    LeaderboardUserDto userDto;

    private LeaderboardGroup userGroup;

    private int positionInCohort;

}
