package com.seerlogics.botadmin.service;

import com.seerlogics.commons.model.Account;
import org.springframework.stereotype.Service;

@Service
public class HelperService {

    private final AccountService accountService;

    public HelperService(AccountService accountService) {
        this.accountService = accountService;
    }

    boolean isAllowedToEdit(Account target) {
        return target.getId().equals(accountService.getAuthenticatedUser().getId());
    }
}
