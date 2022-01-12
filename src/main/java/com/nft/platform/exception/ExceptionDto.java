package com.nft.platform.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDto {

    @Schema(description = "The message of exception")
    private String message;

    @Schema(description = "The http status associated with exception")
    private HttpStatus httpStatus;
}
