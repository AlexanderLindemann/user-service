package com.nft.platform.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ItemConflictException extends RestException {

    public ItemConflictException(@NonNull Class<?> itemClass, @NonNull String name) {
        super(itemClass.getSimpleName() + " with name=" + name + " already exists", HttpStatus.CONFLICT);
    }

    public ItemConflictException(@NonNull Class<?> itemClass, @NonNull UUID id) {
        super(itemClass.getSimpleName() + " already exists id=" + id, HttpStatus.CONFLICT);
    }
}
