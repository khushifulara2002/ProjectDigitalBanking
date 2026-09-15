// 2. InsufficientBalanceException.java (HTTP 400)
package com.bank.digitalbanking.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}