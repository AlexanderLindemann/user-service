package com.nft.platform.controller;

import com.nft.platform.dto.CelebrityFanDto;
import com.nft.platform.service.FanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Celebrity's fans API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/celebrity/fans")
public class FansController {
    private final FanService fanService;

    @GetMapping(path = "/top")
    @Operation(summary = "Get top 3 fans of celebrity and sum for top 100")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CelebrityFanDto>> getCelebrityTopFans(@RequestParam(name = "celebrityId") UUID id) {
        return ResponseEntity.ok(fanService.getTopFansWithSum(id, 3, 100));
    }
}
