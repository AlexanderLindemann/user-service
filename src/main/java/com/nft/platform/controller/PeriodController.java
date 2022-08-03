package com.nft.platform.controller;

import com.nft.platform.common.dto.PeriodResponseDto;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.service.PeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "Period Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/periods")
public class PeriodController {

    private final PeriodService periodService;

    @GetMapping("/current")
    @Operation(summary = "Get Current Period")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PeriodResponseDto> getCurrentPeriod() {
        Optional<PeriodResponseDto> periodResponseDtoO = periodService.findPeriod(PeriodStatus.ACTIVE);
        return ResponseEntity.of(periodResponseDtoO);
    }

    @GetMapping("/next")
    @Operation(summary = "Get Next Period start time")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PeriodResponseDto> getNextPeriod() {
        Optional<PeriodResponseDto> periodResponseDtoO = periodService.findPeriod(PeriodStatus.NEXT);
        return ResponseEntity.of(periodResponseDtoO);
    }

    @GetMapping("/endtime")
    @Operation(summary = "Get end time of active Period.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getActivePeriodEndTime() {
        return ResponseEntity.ok(periodService.getEndPeriod());
    }

}