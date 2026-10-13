package com.pismo.techcase.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pismo.techcase.account.Account;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TransactionTest {

    private static final Account ACCOUNT = new Account("12345678900");
    private static final Instant EVENT_DATE = Instant.parse("2020-01-01T10:00:00Z");

    @Test
    void startsWithBalanceEqualToAmount() {
        //inicia o saldo igual ao crédito

        Transaction transaction = transaction(OperationType.NORMAL_PURCHASE, "-100.00");
        assertThat(transaction.getBalance()).isEqualByComparingTo("-100.00");
    }

    @Test
    void partiallyDischargesADebit() {
        //Liquida parcialmente um débito

        Transaction debit = transaction(OperationType.NORMAL_PURCHASE, "-100.00");
        Transaction credit = transaction(OperationType.CREDIT_VOUCHER, "60.00");

        credit.discharge(debit);

        assertThat(debit.getAmount()).isEqualByComparingTo("-100.00");
        assertThat(debit.getBalance()).isEqualByComparingTo("-40.00");
        assertThat(credit.getAmount()).isEqualByComparingTo("60.00");
        assertThat(credit.getBalance()).isZero();
    }

    @Test
    void keepsTheRemainingCreditAfterFullyDischargingADebit() {
        //mantém o crédito restante após a quitação integral de um débito

        Transaction debit = transaction(OperationType.WITHDRAWAL, "-100.00");
        Transaction credit = transaction(OperationType.CREDIT_VOUCHER, "150.00");

        credit.discharge(debit);

        assertThat(debit.getBalance()).isZero();
        assertThat(credit.getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    void preventsADebitFromStartingADischarge() {
        Transaction debit = transaction(OperationType.NORMAL_PURCHASE, "-100.00");
        Transaction anotherDebit = transaction(OperationType.WITHDRAWAL, "-50.00");

        assertThatThrownBy(() -> debit.discharge(anotherDebit))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Only credit transactions can discharge debits");
    }
    private Transaction transaction(OperationType operationType, String amount) {
        return new Transaction(
            ACCOUNT,
            operationType,
            new BigDecimal(amount),
            EVENT_DATE
        );
    }
}