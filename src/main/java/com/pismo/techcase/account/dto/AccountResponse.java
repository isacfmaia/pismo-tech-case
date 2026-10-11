package com.pismo.techcase.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pismo.techcase.account.Account;

public record AccountResponse(
    @JsonProperty("account_id") long accountId,
    @JsonProperty("document_number") String documentNumber
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getDocumentNumber());
    }
}