package com.pismo.techcase.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
    @JsonProperty("document_number")
    @NotBlank(message = "document_number must not be blank")
    @Size(max = 50, message = "document_number must have at most 50 characters")
    String documentNumber
) {
}