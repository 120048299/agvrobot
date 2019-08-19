package com.wootion.exceptions;

public class HaveNotAuthException extends RuntimeException {
    public HaveNotAuthException() {
    }

    public HaveNotAuthException(String message) {
        super(message);
    }
}
