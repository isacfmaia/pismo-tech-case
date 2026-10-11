package com.pismo.techcase.transaction;

import com.pismo.techcase.shared.exception.InvalidOperationTypeException;
import com.pismo.techcase.shared.exception.InvalidTransactionAmountException;
import java.math.BigDecimal;
import java.util.Arrays;

public enum OperationType {
    NORMAL_PURCHASE(1, BalanceEffect.DEBIT),
    PURCHASE_WITH_INSTALLMENTS(2, BalanceEffect.DEBIT),
    WITHDRAWAL(3, BalanceEffect.DEBIT),
    CREDIT_VOUCHER(4, BalanceEffect.CREDIT);

    private final int id;
    private final BalanceEffect balanceEffect;

    OperationType(int id, BalanceEffect balanceEffect) {
        this.id = id;
        this.balanceEffect = balanceEffect;
    }

    public int getId() {
        return id;
    }

    public BigDecimal applySign(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidTransactionAmountException();
        }

        return balanceEffect == BalanceEffect.DEBIT ? amount.negate() : amount;
    }

    public static OperationType fromId(int id) {
        return Arrays.stream(values())
            .filter(operationType -> operationType.id == id)
            .findFirst()
            .orElseThrow(() -> new InvalidOperationTypeException(id));
    }

    private enum BalanceEffect {
        DEBIT,
        CREDIT
    }
}
