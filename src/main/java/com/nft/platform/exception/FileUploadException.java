package com.nft.platform.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public class FileUploadException extends RestException {

    public FileUploadException(@NonNull String operation, @NonNull String reason) {
        super(operation + " can't be proceed on file because of : " + reason, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
