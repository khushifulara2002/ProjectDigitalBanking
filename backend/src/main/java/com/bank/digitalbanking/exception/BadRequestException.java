// 4. BadRequestException.java (HTTP 400)
package com.bank.digitalbanking.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}