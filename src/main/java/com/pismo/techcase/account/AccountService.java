package com.pismo.techcase.account;

import com.pismo.techcase.shared.exception.AccountAlreadyExistsException;
import com.pismo.techcase.shared.exception.AccountNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(String documentNumber) {
        String normalizedDocumentNumber = documentNumber.trim();

        if (accountRepository.existsByDocumentNumber(normalizedDocumentNumber)) {
            throw new AccountAlreadyExistsException(normalizedDocumentNumber);
        }

        return accountRepository.save(new Account(normalizedDocumentNumber));
    }

    @Transactional(readOnly = true)
    public Account findAccount(long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
