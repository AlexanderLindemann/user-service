package com.nft.platform.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class BadRequestException extends RestException {

    public BadRequestException(@NonNull Class<?> itemClass, @NonNull String operation, @NonNull String reason) {
        super(operation + " can't be proceed on " + itemClass.getSimpleName() + " because of : " + reason, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(@NonNull Class<?> itemClass, @NonNull UUID id, @NonNull String operation, @NonNull String reason) {
        super(operation + " can't be proceed on " + itemClass.getSimpleName() + " with id = " + id + " because of : " + reason, HttpStatus.BAD_REQUEST);
    }
}
