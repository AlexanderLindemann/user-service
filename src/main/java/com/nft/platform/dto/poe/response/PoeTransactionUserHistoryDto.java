package com.nft.platform.dto.poe.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Poe History DTO")
public class PoeTransactionUserHistoryDto {

    private String poeName;
    private int points;
    private LocalDateTime createdAt;
}
