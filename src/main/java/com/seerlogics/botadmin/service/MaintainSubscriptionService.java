package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.dto.AccountDetail;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.MembershipPlan;
import com.seerlogics.commons.model.Role;
import com.seerlogics.commons.model.Subscription;
import com.seerlogics.commons.repository.MembershipPlanRepository;
import com.seerlogics.commons.repository.SubscriptionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintainSubscriptionService extends BaseServiceImpl {

    private final SubscriptionRepository subscriptionRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final AccountService accountService;
    private final HelperService helperService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public MaintainSubscriptionService(SubscriptionRepository subscriptionRepository, MembershipPlanRepository membershipPlanRepository, AccountService accountService, HelperService helperService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.subscriptionRepository = subscriptionRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.accountService = accountService;
        this.helperService = helperService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountDetail initAccountDetail(String type) {
        AccountDetail accountDetail = new AccountDetail();
        Account account = this.accountService.initAccount(type);
        accountDetail.setAccount(account);
        Subscription subscription = new Subscription();
        accountDetail.setSubscription(subscription);

        List<MembershipPlan> membershipPlanList = this.membershipPlanRepository.findAll();
        accountDetail.getReferenceData().put("plans", membershipPlanList);

        return accountDetail;
    }

    public AccountDetail saveAccountDetail(AccountDetail accountDetail) {
        Subscription subscription = new Subscription();
        accountDetail.getAccount().setPassword(passwordEncoder.encode(accountDetail.getAccount().getPasswordCapture()));
        subscription.setOwner(accountDetail.getAccount());

        Role accountAdmin = this.roleService.findByCode("ACCT_ADMIN");
        accountDetail.getAccount().getRoles().clear();
        accountDetail.getAccount().getRoles().add(accountAdmin);

        subscription.setPlan(this.membershipPlanRepository.findByCode(accountDetail.getMembershipPlanCode()));
        this.subscriptionRepository.save(subscription);
        return accountDetail;
    }
}
