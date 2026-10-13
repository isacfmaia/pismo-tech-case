package com.pismo.techcase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pismo.techcase.support.PostgreSqlIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class OpenApiIntegrationTest extends PostgreSqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void documentsApiFieldsInSnakeCase() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.components.schemas.CreateAccountRequest.properties.document_number").exists())
            .andExpect(jsonPath("$.components.schemas.CreateAccountRequest.properties.documentNumber").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.AccountResponse.properties.account_id").exists())
            .andExpect(jsonPath("$.components.schemas.AccountResponse.properties.document_number").exists())
            .andExpect(jsonPath("$.components.schemas.CreateTransactionRequest.properties.account_id").exists())
            .andExpect(jsonPath("$.components.schemas.CreateTransactionRequest.properties.operation_type_id").exists())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.transaction_id").exists())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.account_id").exists())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.operation_type_id").exists())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.balance").exists())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.event_date").exists())
            .andExpect(jsonPath("$.components.schemas.AccountResponse.properties.accountId").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.AccountResponse.properties.documentNumber").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.CreateTransactionRequest.properties.accountId").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.CreateTransactionRequest.properties.operationTypeId").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.transactionId").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.accountId").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.operationTypeId").doesNotExist())
            .andExpect(jsonPath("$.components.schemas.TransactionResponse.properties.eventDate").doesNotExist());
    }
}