package com.nft.platform.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(1)
public class RestControllerAdviceExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ExceptionDto> handleException(RestException ex) {
        log.error("Caught rest exception {}", ex.toString());

        var exceptionDto = ExceptionDto.builder()
                .message(ex.getMsg())
                .httpStatus(ex.getHttpStatus())
                .build();

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(exceptionDto)
                ;
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ExceptionDto> handleFeignStatusException(FeignException ex) {
        log.error("Caught feign exception {}", ex.toString());

        var status = HttpStatus.resolve(ex.status());
        var exceptionDto = ExceptionDto.builder()
                .message("Error occurred while " + ex.request().httpMethod() + " to '" + ex.request().url() + ex.getMessage() + "': " + ex.contentUTF8())
                .httpStatus(status)
                .build();

        return ResponseEntity
                .status(status)
                .body(exceptionDto)
                ;
    }
}
