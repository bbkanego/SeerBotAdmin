package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.model.Account;
import com.seerlogics.botadmin.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private AccountService accountService;

    @PostMapping(value = {"", "/signup",})
    public ResponseEntity<String> save(@RequestBody Account category) {
        this.accountService.save(category);
        return returnSuccessResponse();
    }

    @PreAuthorize("hasRole('UBER_ADMIN', 'ADMIN')")
    @GetMapping(value = {"", "/",})
    public ResponseEntity<Collection<Account>> getAll() {
        return new ResponseEntity<>(this.accountService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Account> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(this.accountService.getSingle(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/username/{username}")
    public ResponseEntity<Account> getByUserName(@PathVariable("username") String username) {
        return new ResponseEntity<>(this.accountService.getAccountByUsername(username), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN', 'UBER_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        this.accountService.delete(id);
        return returnSuccessResponse();
    }

    @ResponseBody
    @GetMapping(value = "/init/{type}")
    public Account initAddress(@PathVariable String type) {
        return this.accountService.initAccount(type);
    }
}
