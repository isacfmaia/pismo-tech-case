package com.pismo.techcase.transaction;

import com.pismo.techcase.account.Account;
import com.pismo.techcase.account.AccountRepository;
import com.pismo.techcase.shared.exception.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Clock clock;

    public TransactionService(
        AccountRepository accountRepository,
        TransactionRepository transactionRepository,
        Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.clock = clock;
    }

    @Transactional
    public Transaction createTransaction(
        long accountId,
        int operationTypeId,
        BigDecimal amount
    ) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));
        OperationType operationType = OperationType.fromId(operationTypeId);
        BigDecimal signedAmount = operationType.applySign(amount);

        Transaction transaction = new Transaction(
            account,
            operationType,
            signedAmount,
            Instant.now(clock)
        );

        if (operationType.isCredit()) {
            dischargeOutstandingDebits(transaction);
        }

        return transactionRepository.save(transaction);
    }

    private void dischargeOutstandingDebits(Transaction credit) {
        List<Transaction> outstandingDebits = transactionRepository.findOutstandingDebitsForUpdate(
            credit.getAccount().getId()
        );

        for (Transaction debit : outstandingDebits) {
            if (!credit.hasAvailableBalance()) {
                break;
            }

            credit.discharge(debit);
        }
    }
}
