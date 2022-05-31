package com.nft.platform.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardV2ResponseDto {

    @Builder.Default
    private List<LeaderboardPositionDto> firstBlock = new ArrayList<>();

    @Builder.Default
    private List<LeaderboardPositionDto> secondBlock = new ArrayList<>();

    @Builder.Default
    private List<LeaderboardPositionDto> thirdBlock = new ArrayList<>();
}
