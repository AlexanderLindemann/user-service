package com.nft.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardByIdResponseDto {

    private LeaderboardByIdDto otherUser;
    private LeaderboardByIdDto currentUser;
}
