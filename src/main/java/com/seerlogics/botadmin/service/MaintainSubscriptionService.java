package com.seerlogics.botadmin.service;

import com.seerlogics.commons.dto.AccountDetail;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.Subscription;
import com.seerlogics.commons.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class MaintainSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AccountService accountService;

    public MaintainSubscriptionService(SubscriptionRepository subscriptionRepository, AccountService accountService) {
        this.subscriptionRepository = subscriptionRepository;
        this.accountService = accountService;
    }

    public AccountDetail initAccountDetail(String type) {
        AccountDetail accountDetail = new AccountDetail();
        Account account = this.accountService.initAccount(type);
        accountDetail.setAccount(account);
        Subscription subscription = new Subscription();
        accountDetail.setSubscription(subscription);
        return accountDetail;
    }
}
