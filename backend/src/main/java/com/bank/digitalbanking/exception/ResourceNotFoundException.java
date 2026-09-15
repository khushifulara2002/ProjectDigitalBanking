// 1. ResourceNotFoundException.java (HTTP 404)
package com.bank.digitalbanking.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}