package com.pismo.techcase.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pismo.techcase.support.PostgreSqlIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class AccountApiIntegrationTest extends PostgreSqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void createsAnAccount() throws Exception {
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"document_number":"12345678900"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/accounts/\\d+")))
            .andExpect(jsonPath("$.account_id").isNumber())
            .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    void rejectsAnInvalidDocumentNumber() throws Exception {
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"document_number":"  "}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("document_number must not be blank"))
            .andExpect(jsonPath("$.path").value("/accounts"));
    }

    @Test
    void rejectsADuplicateDocumentNumber() throws Exception {
        accountRepository.save(new Account("12345678900"));

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"document_number":"12345678900"}
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value("An account with document_number 12345678900 already exists"));
    }

    @Test
    void findsAnExistingAccount() throws Exception {
        Account account = accountRepository.save(new Account("12345678900"));

        mockMvc.perform(get("/accounts/{accountId}", account.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.account_id").value(account.getId()))
            .andExpect(jsonPath("$.document_number").value("12345678900"));
    }

    @Test
    void returnsNotFoundForAnUnknownAccount() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}", 999999))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Account 999999 was not found"))
            .andExpect(jsonPath("$.path").value("/accounts/999999"));
    }
}
