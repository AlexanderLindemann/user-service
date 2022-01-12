package com.nft.platform.dto;

import ch.qos.logback.classic.Logger;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LoggerManageDto {

    @Schema(description = "The logger name", example = "com.nft.platform.service.SampleServiceImpl")
    private String name;

    @Schema(description = "The logger level", example = "DEBUG")
    private String level;

    public LoggerManageDto(Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }
}
