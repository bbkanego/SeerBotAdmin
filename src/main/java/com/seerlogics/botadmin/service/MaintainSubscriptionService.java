package com.seerlogics.botadmin.service;

import com.lingoace.common.EntityNotFoundException;
import com.lingoace.common.exception.NotAuthorizedException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.lingoace.validation.ValidationException;
import com.lingoace.validation.ValidationResult;
import com.seerlogics.commons.CommonConstants;
import com.seerlogics.commons.dto.AccountDetail;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.MembershipPlan;
import com.seerlogics.commons.model.Role;
import com.seerlogics.commons.model.Subscription;
import com.seerlogics.commons.repository.AccountRepository;
import com.seerlogics.commons.repository.MembershipPlanRepository;
import com.seerlogics.commons.repository.SubscriptionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintainSubscriptionService extends BaseServiceImpl {
    private final SubscriptionRepository subscriptionRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final HelperService helperService;

    public MaintainSubscriptionService(SubscriptionRepository subscriptionRepository,
                                       MembershipPlanRepository membershipPlanRepository,
                                       AccountService accountService,
                                       AccountRepository accountRepository, RoleService roleService, PasswordEncoder passwordEncoder, HelperService helperService) {
        this.subscriptionRepository = subscriptionRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.helperService = helperService;
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

        Account existingAccount = this.accountRepository.findByUserName(accountDetail.getAccount().getUserName());
        if (existingAccount != null) {
            String userNameAlreadyExists =
                    this.helperService.getMessage("message.username.already.exists", new String[]{});
            ValidationResult validationResult = new ValidationResult();
            validationResult.addPageLevelError(userNameAlreadyExists);
            validationResult.addFieldError("userName", "userName", userNameAlreadyExists);
            throw new ValidationException(userNameAlreadyExists, validationResult);
        }

        accountDetail.getAccount().setPassword(passwordEncoder.encode(accountDetail.getAccount().getPasswordCapture()));
        subscription.setOwner(accountDetail.getAccount());

        Role accountAdmin = this.roleService.findByCode("ACCT_ADMIN");
        accountDetail.getAccount().getRoles().clear();
        accountDetail.getAccount().getRoles().add(accountAdmin);

        subscription.setPlan(this.membershipPlanRepository.findByCode(accountDetail.getMembershipPlanCode()));
        this.subscriptionRepository.save(subscription);
        return accountDetail;
    }

    @PreAuthorize(CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public AccountDetail getAccountDetailByAccountId(Long accountId) {
        Subscription subscription = this.subscriptionRepository.findByOwnerId(accountId);
        if (subscription == null) {
            throw new EntityNotFoundException("Subscription not found for id = " + accountId);
        }
        return loadAccountDetail(subscription);
    }

    private AccountDetail loadAccountDetail(Subscription subscription) {
        if (!this.helperService.isAllowedToEdit(subscription.getOwner())) {
            throw new NotAuthorizedException();
        }
        AccountDetail accountDetail = new AccountDetail();
        //accountDetail.setAccount(subscription.getOwner());
        accountDetail.setSubscription(subscription);
        return accountDetail;
    }

    @PreAuthorize(CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public AccountDetail getAccountDetailByAccountUserName(String userName) {
        Subscription subscription = this.subscriptionRepository.findByOwnerUserName(userName);
        if (subscription == null) {
            throw new EntityNotFoundException("Subscription not found for userName = " + userName);
        }
        return loadAccountDetail(subscription);
    }
}
