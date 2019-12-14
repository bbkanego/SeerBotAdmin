package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.MembershipPlan;
import com.seerlogics.commons.model.Tier;
import com.seerlogics.commons.repository.MembershipPlanRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional("botAdminTransactionManager")
public class MembershipPlanService extends BaseServiceImpl<MembershipPlan> {

    private final MembershipPlanRepository membershipPlanRepository;
    private final TierService tierService;
    private final HelperService helperService;

    public MembershipPlanService(MembershipPlanRepository membershipPlanRepository, TierService tierService, HelperService helperService) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.tierService = tierService;
        this.helperService = helperService;
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public MembershipPlan initMembershipPlan() {
        MembershipPlan membershipPlan = new MembershipPlan();
        this.addReferenceData(membershipPlan);
        return membershipPlan;
    }

    private void addReferenceData(MembershipPlan membershipPlan) {
        membershipPlan.getReferenceData().put("tiers",
                this.helperService.buildReferenceData(this.tierService.getAll()));
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public MembershipPlan saveMembershipPlan(MembershipPlan membershipPlan) {
        Tier tier = this.tierService.findByCode(membershipPlan.getTierCode());
        membershipPlan.setTier(tier);
        membershipPlan.setCode("PLAN-" + helperService.generateRandomCode());
        return this.membershipPlanRepository.save(membershipPlan);
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public void deleteMembershipPlan(MembershipPlan membershipPlan) {
        this.membershipPlanRepository.delete(membershipPlan);
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public void deleteMembershipPlan(Long planId) {
        this.membershipPlanRepository.deleteById(planId);
    }

    public Collection<MembershipPlan> getAllPlans() {
        return this.membershipPlanRepository.findAll();
    }

    @Override
    public MembershipPlan getSingle(Long id) {
        MembershipPlan membershipPlan = this.membershipPlanRepository.getOne(id);
        membershipPlan.setTierCode(membershipPlan.getTier().getCode());
        addReferenceData(membershipPlan);
        return membershipPlan;
    }
}
