package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.MaintainSubscriptionService;
import com.seerlogics.commons.CommonConstants;
import com.seerlogics.commons.dto.AccountDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/subscription")
public class SubscriptionController extends BaseController {

    private final MaintainSubscriptionService maintainSubscriptionService;

    public SubscriptionController(MaintainSubscriptionService maintainSubscriptionService) {
        this.maintainSubscriptionService = maintainSubscriptionService;
    }

    @GetMapping(value = "/init/{type}")
    public AccountDetail initAccountDetail(@PathVariable String type) {
        return this.maintainSubscriptionService.initAccountDetail(type);
    }

    @PostMapping(value = {"/signup"})
    public AccountDetail createAccountDetail(@RequestBody AccountDetail accountDetail) {
        return this.maintainSubscriptionService.saveAccountDetail(accountDetail);
    }

    @GetMapping(value = {"/{accountId}"})
    @PreAuthorize(CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public AccountDetail getAccountDetail(@PathVariable Long accountId) {
        return this.maintainSubscriptionService.getAccountDetail(accountId);
    }
}
