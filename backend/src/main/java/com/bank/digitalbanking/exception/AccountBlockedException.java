// 3. AccountBlockedException.java (HTTP 400)
package com.bank.digitalbanking.exception;

public class AccountBlockedException extends RuntimeException {
  public AccountBlockedException(String message) {
    super(message);
  }
}