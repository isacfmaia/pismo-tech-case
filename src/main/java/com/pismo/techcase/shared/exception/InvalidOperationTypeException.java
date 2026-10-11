package com.pismo.techcase.shared.exception;

public class InvalidOperationTypeException extends RuntimeException {

    public InvalidOperationTypeException(int operationTypeId) {
        super("operation_type_id " + operationTypeId + " is invalid");
    }
}
