package com.pismo.techcase.shared.exception;

public class InvalidTransactionAmountException extends RuntimeException {

    public InvalidTransactionAmountException() {
        super("amount must be greater than zero");
    }
}
