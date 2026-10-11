package com.pismo.techcase.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pismo.techcase.shared.exception.InvalidTransactionAmountException;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OperationTypeTest {

    @ParameterizedTest(name = "{0} transforms 100.00 into {1}")
    @MethodSource("signedAmounts")
    void appliesTheExpectedSign(OperationType operationType, String expectedAmount) {
        BigDecimal result = operationType.applySign(new BigDecimal("100.00"));

        assertThat(result).isEqualByComparingTo(expectedAmount);
    }

    @Test
    void rejectsNonPositiveAmounts() {
        assertThatThrownBy(() -> OperationType.NORMAL_PURCHASE.applySign(BigDecimal.ZERO))
            .isInstanceOf(InvalidTransactionAmountException.class)
            .hasMessage("amount must be greater than zero");
    }

    private static Stream<Arguments> signedAmounts() {
        return Stream.of(
            Arguments.of(OperationType.NORMAL_PURCHASE, "-100.00"),
            Arguments.of(OperationType.PURCHASE_WITH_INSTALLMENTS, "-100.00"),
            Arguments.of(OperationType.WITHDRAWAL, "-100.00"),
            Arguments.of(OperationType.CREDIT_VOUCHER, "100.00")
        );
    }
}
