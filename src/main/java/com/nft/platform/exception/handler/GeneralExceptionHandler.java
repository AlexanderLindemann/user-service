package com.nft.platform.exception.handler;

import com.nft.platform.exception.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(100)
@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDto> handleRuntimeException(RuntimeException ex) {
        log.error("Caught runtime exception {}", ex.toString());
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .message(message)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return ResponseEntity
                .status(500)
                .body(exceptionDto);
    }
}
