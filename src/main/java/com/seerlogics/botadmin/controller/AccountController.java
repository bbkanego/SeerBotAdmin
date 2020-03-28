package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.service.AccountService;
import com.seerlogics.commons.dto.ChangePassword;
import com.seerlogics.commons.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@RestController
@RequestMapping(value = "/api/v1/account")
public class AccountController extends BaseController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(value = {"", "/signup",})
    public ResponseEntity save(@RequestBody Account account) {
        this.accountService.save(account);
        return returnSuccessResponse();
    }

    @GetMapping(value = {"", "/",})
    public ResponseEntity<Collection<Account>> getAll() {
        return new ResponseEntity<>(this.accountService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ACCT_USER', 'ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Account> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(this.accountService.getSingle(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/username/{username}")
    public ResponseEntity<Account> getByUserName(@PathVariable("username") String username) {
        return new ResponseEntity<>(this.accountService.getAccountByUsername(username), HttpStatus.OK);
    }

    @GetMapping(value = "/init/{type}")
    public Account initAddress(@PathVariable String type) {
        return this.accountService.initAccount(type);
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        this.accountService.delete(id);
        return returnSuccessResponse();
    }

    @PostMapping(value = {"/changePassword"})
    public ResponseEntity changePassword(@Validate("validateChangePassword") @RequestBody ChangePassword changePassword) {
        boolean passwordChanged = this.accountService.changePassword(changePassword);
        if (passwordChanged) {
            return returnSuccessResponse();
        } else {
            return returnFailureResponse();
        }
    }
}
