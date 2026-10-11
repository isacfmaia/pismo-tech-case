package com.pismo.techcase.shared.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String documentNumber) {
        super("An account with document_number " + documentNumber + " already exists");
    }
}
