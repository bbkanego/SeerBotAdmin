package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.MaintainSubscriptionService;
import com.seerlogics.commons.dto.AccountDetail;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/subscription")
public class SubscriptionController extends BaseController {

    private final MaintainSubscriptionService maintainSubscriptionService;

    public SubscriptionController(MaintainSubscriptionService maintainSubscriptionService) {
        this.maintainSubscriptionService = maintainSubscriptionService;
    }

    @ResponseBody
    @GetMapping(value = "/init/{type}")
    public AccountDetail initAccountDetail(@PathVariable String type) {
        return this.maintainSubscriptionService.initAccountDetail(type);
    }

    @ResponseBody
    @PostMapping(value = {"", "/"})
    public AccountDetail createAccountDetail(@PathVariable String type) {
        return this.maintainSubscriptionService.initAccountDetail(type);
    }
}
