package com.pismo.techcase.shared.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long accountId) {
        super("Account " + accountId + " was not found");
    }
}
