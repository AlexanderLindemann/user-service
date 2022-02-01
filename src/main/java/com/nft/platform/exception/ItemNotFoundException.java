package com.nft.platform.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ItemNotFoundException extends RestException {

    public ItemNotFoundException(@NonNull Class<?> itemClass, @NonNull UUID id) {
        super(itemClass.getSimpleName() + " with Id=" + id + " not found", HttpStatus.NOT_FOUND);
    }

    public ItemNotFoundException(@NonNull Class<?> itemClass, @NonNull String params) {
        super(itemClass.getSimpleName() + " with params [" + params + "] not found", HttpStatus.NOT_FOUND);
    }
}
