package com.pismo.techcase.transaction;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OperationTypeConverter implements AttributeConverter<OperationType, Short> {

    @Override
    public Short convertToDatabaseColumn(OperationType operationType) {
        return operationType == null ? null : (short) operationType.getId();
    }

    @Override
    public OperationType convertToEntityAttribute(Short operationTypeId) {
        return operationTypeId == null ? null : OperationType.fromId(operationTypeId);
    }
}
