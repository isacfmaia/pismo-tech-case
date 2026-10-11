package com.pismo.techcase.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pismo.techcase.transaction.Transaction;
import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
    @JsonProperty("transaction_id") long transactionId,
    @JsonProperty("account_id") long accountId,
    @JsonProperty("operation_type_id") int operationTypeId,
    BigDecimal amount,
    @JsonProperty("event_date") Instant eventDate
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAccount().getId(),
            transaction.getOperationType().getId(),
            transaction.getAmount(),
            transaction.getEventDate()
        );
    }
}