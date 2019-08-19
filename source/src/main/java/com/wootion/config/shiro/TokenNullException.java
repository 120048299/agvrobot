package com.wootion.config.shiro;

public class TokenNullException extends RuntimeException {
    public TokenNullException() {
    }

    public TokenNullException(String message) {
        super(message);
    }
}
