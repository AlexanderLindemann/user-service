package com.nft.platform.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ItemValidationException extends RestException {

    public ItemValidationException(@NonNull Class<?> itemClass, @NonNull UUID id) {
        super(itemClass.getSimpleName() + " with Id=" + id + " validation failed", HttpStatus.BAD_REQUEST);
    }

    public ItemValidationException(@NonNull Class<?> itemClass, @NonNull String name) {
        super(itemClass.getSimpleName() + " with property=" + name + " validation failed", HttpStatus.BAD_REQUEST);
    }
}
