package com.nft.platform.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.nft.platform.dto.LoggerManageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/v1/logs")
@RestController
@Tag(name = "logging-api", description = "The service helper API with logging managing")
public class LoggingManageController {

    @Operation(summary = "Get all loggers that available to manage")
    @GetMapping
    public List<LoggerManageDto> getList() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLoggerList()
                .stream()
                .map(LoggerManageDto::new)
                .collect(Collectors.toList())
                ;
    }

    @Operation(summary = "Manage logger configuration")
    @PutMapping
    public void manageLogger(@RequestBody LoggerManageDto jsonLogger) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    }
}
