package com.mycorp.utils;


public class ZendeskException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ZendeskException(String message) {
        super(message);
    }

    public ZendeskException(Throwable cause) {
        super(cause);
    }

    public ZendeskException(String message, Throwable cause) {
        super(message, cause);
    }
}
