package com.seerlogics.botadmin.service;

import com.seerlogics.commons.model.MembershipPlan;
import com.seerlogics.commons.model.Tier;
import com.seerlogics.commons.repository.MembershipPlanRepository;
import com.seerlogics.commons.repository.TierRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("botAdminTransactionManager")
@PreAuthorize("hasAnyRole('UBER_ADMIN')")
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final TierRepository tierRepository;

    public MembershipPlanService(MembershipPlanRepository membershipPlanRepository, TierRepository tierRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.tierRepository = tierRepository;
    }

    public void saveTier(Tier tier) {
        this.tierRepository.save(tier);
    }

    public void saveMembershipPlan(MembershipPlan membershipPlan) {
        this.membershipPlanRepository.save(membershipPlan);
    }
}
