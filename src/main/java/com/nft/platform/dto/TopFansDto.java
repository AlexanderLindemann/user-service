package com.nft.platform.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TopFansDto {
    List<CelebrityFanDto> fans;
    CelebrityFanDto fansSum;
}