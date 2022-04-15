package com.nft.platform.feign.client;

import com.nft.platform.dto.response.ShowcaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = NftServiceApiClient.NAME,
        url = "${feign." + NftServiceApiClient.NAME + ".url}"
)
public interface NftServiceApiClient {
    String NAME = "nft-service-api";

    @GetMapping(path = "/api/v1/nfts/getShowcase")
    List<ShowcaseResponseDto> getShowcase(@RequestParam("celebrityIds") List<UUID> celebrityIds);
}
