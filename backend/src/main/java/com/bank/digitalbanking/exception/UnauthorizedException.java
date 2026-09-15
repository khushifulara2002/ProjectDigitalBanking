// 5. UnauthorizedException.java (HTTP 401)
package com.bank.digitalbanking.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}