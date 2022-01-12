package com.nft.platform.initializer;

public class TestInitializationException extends RuntimeException{
    public TestInitializationException() {
    }

    public TestInitializationException(String message) {
        super(message);
    }

    public TestInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestInitializationException(Throwable cause) {
        super(cause);
    }
}
