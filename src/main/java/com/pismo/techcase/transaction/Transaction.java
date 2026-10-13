package com.pismo.techcase.transaction;

import com.pismo.techcase.account.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Convert(converter = OperationTypeConverter.class)
    @Column(name = "operation_type_id", nullable = false)
    private OperationType operationType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    protected Transaction() {
    }

    public Transaction(
        Account account,
        OperationType operationType,
        BigDecimal amount,
        Instant eventDate
    ) {
        this.account = account;
        this.operationType = operationType;
        this.amount = amount;
        this.balance = amount;
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    boolean hasAvailableBalance() {
        return operationType.isCredit() && balance.signum() > 0;
    }

    void discharge(Transaction debit) {
        if (!operationType.isCredit()) {
            throw new IllegalStateException("Only credit transactions can discharge debits");
        }

        if (!hasAvailableBalance() || debit.balance.signum() >= 0) {
            return;
        }

        BigDecimal dischargedAmount = balance.min(debit.balance.abs());
        balance = balance.subtract(dischargedAmount);
        debit.balance = debit.balance.add(dischargedAmount);
    }

    public Instant getEventDate() {
        return eventDate;
    }
}
