package com.pismo.techcase.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateTransactionRequest(
    @JsonProperty("account_id")
    @NotNull(message = "account_id is required")
    @Positive(message = "account_id must be greater than zero")
    Long accountId,

    @JsonProperty("operation_type_id")
    @NotNull(message = "operation_type_id is required")
    @Positive(message = "operation_type_id must be greater than zero")
    Integer operationTypeId,

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than zero")
    @Digits(integer = 17, fraction = 2, message = "amount must have at most 17 integer digits and 2 decimal places")
    BigDecimal amount
) {
}