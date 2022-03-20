package com.nft.platform.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Period Response DTO")
public class PeriodResponseDto {

    @Schema(description = "Id", required = true)
    private UUID id;

    @Schema(description = "Start Time", required = true)
    private LocalDateTime startTime;

    @Schema(description = "End Time", required = true)
    private LocalDateTime endTime;
}
