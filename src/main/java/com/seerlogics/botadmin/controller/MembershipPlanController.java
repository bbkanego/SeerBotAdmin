package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.MembershipPlanService;
import com.seerlogics.commons.model.MembershipPlan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/plan")
public class MembershipPlanController extends BaseController {

    private final MembershipPlanService membershipPlanService;

    public MembershipPlanController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }

    @GetMapping(value = "/init")
    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public MembershipPlan init() {
        return this.membershipPlanService.initMembershipPlan();
    }

    @GetMapping(value = "/getAll")
    public Collection<MembershipPlan> getAll() {
        return this.membershipPlanService.getAllPlans();
    }

    @PostMapping(value = {"/", ""})
    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public MembershipPlan save(@RequestBody MembershipPlan membershipPlan) {
        return this.membershipPlanService.saveMembershipPlan(membershipPlan);
    }

    @DeleteMapping(value = "/{planId}")
    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public ResponseEntity deleteById(@PathVariable Long planId) {
        this.membershipPlanService.delete(planId);
        return returnSuccessResponse();
    }

    @GetMapping(value = "/{planId}")
    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public MembershipPlan getPlanById(@PathVariable Long planId) {
        return this.membershipPlanService.getSingle(planId);
    }
}
