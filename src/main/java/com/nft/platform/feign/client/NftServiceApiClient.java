package com.nft.platform.feign.client;

import com.nft.platform.dto.response.NftCountResponseDto;
import com.nft.platform.dto.response.ShowcaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = NftServiceApiClient.NAME,
        url = "${feign." + NftServiceApiClient.NAME + ".url}"
)
public interface NftServiceApiClient {
    String NAME = "nft-service-api";

    @GetMapping(path = "/api/v1/nfts/showcase")
    List<ShowcaseResponseDto> getShowcase(@RequestParam("celebrityIds") List<UUID> celebrityIds);

    @GetMapping(path = "/api/v1/nfts/nftCount")
    List<NftCountResponseDto> getNftCount(@RequestParam("celebrityIds") List<UUID> celebrityIds);

    @GetMapping(path = "api/v1/nfts/top-celebrity-ids-by-nft-count")
    Map<UUID, Integer> getTopCelebrityIdsByNftCount(@RequestParam int limit);

}
