package com.pismo.techcase.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pismo.techcase.account.Account;
import com.pismo.techcase.account.AccountRepository;
import com.pismo.techcase.support.PostgreSqlIntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class TransactionApiIntegrationTest extends PostgreSqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account account;

    @BeforeEach
    void createAccount() {
        account = accountRepository.save(new Account("12345678900"));
    }

    @Test
    void createsATransactionWithTheSignedAmount() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "account_id": %d,
                      "operation_type_id": 1,
                      "amount": 100.00
                    }
                    """.formatted(account.getId())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.transaction_id").isNumber())
            .andExpect(jsonPath("$.account_id").value(account.getId()))
            .andExpect(jsonPath("$.operation_type_id").value(1))
            .andExpect(jsonPath("$.amount").value(-100))
            .andExpect(jsonPath("$.balance").value(-100))
            .andExpect(jsonPath("$.event_date").isString());

        Transaction savedTransaction = transactionRepository.findAll().getFirst();
        assertThat(savedTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("-100.00"));
        assertThat(savedTransaction.getBalance()).isEqualByComparingTo(new BigDecimal("-100.00"));
        assertThat(savedTransaction.getOperationType()).isEqualTo(OperationType.NORMAL_PURCHASE);
    }

    @Test
    void dischargesTheOldestOutstandingDebitsFirst() throws Exception {
        createTransaction(account.getId(), 1, "50.00");
        createTransaction(account.getId(), 2, "23.50");
        createTransaction(account.getId(), 3, "18.70");

        createTransaction(account.getId(), 4, "60.00");

        List<Transaction> transactions = transactionsInCreationOrder();
        assertThat(transactions).hasSize(4);
        assertThat(transactions.get(0).getAmount()).isEqualByComparingTo("-50.00");
        assertThat(transactions.get(0).getBalance()).isZero();
        assertThat(transactions.get(1).getAmount()).isEqualByComparingTo("-23.50");
        assertThat(transactions.get(1).getBalance()).isEqualByComparingTo("-13.50");
        assertThat(transactions.get(2).getBalance()).isEqualByComparingTo("-18.70");
        assertThat(transactions.get(3).getAmount()).isEqualByComparingTo("60.00");
        assertThat(transactions.get(3).getBalance()).isZero();
    }

    @Test
    void keepsTheRemainingBalanceWhenCreditExceedsOutstandingDebits() throws Exception {
        createTransaction(account.getId(), 1, "30.00");

        createTransaction(account.getId(), 4, "100.00");

        List<Transaction> transactions = transactionsInCreationOrder();
        assertThat(transactions.get(0).getBalance()).isZero();
        assertThat(transactions.get(1).getBalance()).isEqualByComparingTo("70.00");
    }

    @Test
    void doesNotUseAnExistingCreditWhenANewDebitIsCreated() throws Exception {
        createTransaction(account.getId(), 4, "100.00");

        createTransaction(account.getId(), 1, "50.00");

        List<Transaction> transactions = transactionsInCreationOrder();
        assertThat(transactions.get(0).getBalance()).isEqualByComparingTo("100.00");
        assertThat(transactions.get(1).getBalance()).isEqualByComparingTo("-50.00");
    }

    @Test
    void doesNotDischargeDebitsFromAnotherAccount() throws Exception {
        Account anotherAccount = accountRepository.save(new Account("98765432100"));
        createTransaction(anotherAccount.getId(), 1, "50.00");

        createTransaction(account.getId(), 4, "50.00");

        List<Transaction> transactions = transactionsInCreationOrder();
        assertThat(transactions.get(0).getBalance()).isEqualByComparingTo("-50.00");
        assertThat(transactions.get(1).getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    void rejectsATransactionForAnUnknownAccount() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "account_id": 999999,
                      "operation_type_id": 1,
                      "amount": 100.00
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Account 999999 was not found"));
    }

    @Test
    void rejectsAnInvalidOperationType() throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "account_id": %d,
                      "operation_type_id": 99,
                      "amount": 100.00
                    }
                    """.formatted(account.getId())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("operation_type_id 99 is invalid"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1.00"})
    void rejectsANonPositiveAmount(String amount) throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "account_id": %d,
                      "operation_type_id": 1,
                      "amount": %s
                    }
                    """.formatted(account.getId(), amount)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("amount must be greater than zero"));
    }

    private void createTransaction(long accountId, int operationTypeId, String amount)
        throws Exception {
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "account_id": %d,
                      "operation_type_id": %d,
                      "amount": %s
                    }
                    """.formatted(accountId, operationTypeId, amount)))
            .andExpect(status().isCreated());
    }

    private List<Transaction> transactionsInCreationOrder() {
        return transactionRepository.findAll(Sort.by("id"));
    }
}
