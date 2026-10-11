package com.pismo.techcase.account;

import com.pismo.techcase.account.dto.AccountResponse;
import com.pismo.techcase.account.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
        @Valid @RequestBody CreateAccountRequest request
    ) {
        Account account = accountService.createAccount(request.documentNumber());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{accountId}")
            .buildAndExpand(account.getId())
            .toUri();

        return ResponseEntity.created(location).body(AccountResponse.from(account));
    }

    @GetMapping("/{accountId}")
    public AccountResponse findAccount(@PathVariable long accountId) {
        return AccountResponse.from(accountService.findAccount(accountId));
    }
}
