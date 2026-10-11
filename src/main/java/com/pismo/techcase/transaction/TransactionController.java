package com.pismo.techcase.transaction;

import com.pismo.techcase.transaction.dto.CreateTransactionRequest;
import com.pismo.techcase.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
        @Valid @RequestBody CreateTransactionRequest request
    ) {
        Transaction transaction = transactionService.createTransaction(
            request.accountId(),
            request.operationTypeId(),
            request.amount()
        );

        return TransactionResponse.from(transaction);
    }
}
