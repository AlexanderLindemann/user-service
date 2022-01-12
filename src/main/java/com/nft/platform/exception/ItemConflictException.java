package com.nft.platform.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public class ItemConflictException extends RestException {

    public ItemConflictException(@NonNull Class<?> itemClass, @NonNull String name) {
        super(itemClass.getSimpleName() + " with name=" + name + " already exists", HttpStatus.CONFLICT);
    }
}
