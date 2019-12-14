package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Tier;
import com.seerlogics.commons.repository.TierRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional("botAdminTransactionManager")
public class TierService extends BaseServiceImpl<Tier> {
    private final TierRepository tierRepository;
    private final HelperService helperService;

    public TierService(TierRepository tierRepository, HelperService helperService) {
        this.tierRepository = tierRepository;
        this.helperService = helperService;
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public Tier initTier() {
        Tier tier = new Tier();
        addReferenceData(tier);
        return tier;
    }

    private void addReferenceData(Tier tier) {
        List<Map<String, String>> tierDuration = new ArrayList<>();
        for (Tier.TierDuration tierDurationItem : Tier.TierDuration.values()) {
            Map<String, String> tierDurationTuple = new HashMap<>();
            tierDurationTuple.put("code", tierDurationItem.name());
            tierDurationTuple.put("name", tierDurationItem.name());
            tierDuration.add(tierDurationTuple);
        }
        tier.getReferenceData().put("tierDuration", tierDuration);

        List<Map<String, String>> tierType = new ArrayList<>();
        for (Tier.TierType tierDurationItem : Tier.TierType.values()) {
            Map<String, String> tierTypeTuple = new HashMap<>();
            tierTypeTuple.put("code", tierDurationItem.name());
            tierTypeTuple.put("name", tierDurationItem.name());
            tierType.add(tierTypeTuple);
        }
        tier.getReferenceData().put("tierType", tierType);
    }

    @Override
    public Collection<Tier> getAll() {
        return this.tierRepository.findAll();
    }

    @Override
    public Tier getSingle(Long id) {
        Tier tier = this.tierRepository.getOne(id);
        addReferenceData(tier);
        tier.setTierDurationString(tier.getTierDuration().name());
        tier.setTierTypeString(tier.getTierType().name());
        return tier;
    }

    @Override
    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public Tier save(Tier tier) {
        tier.setTierDuration(Tier.TierDuration.valueOf(tier.getTierDurationString()));
        tier.setTierType(Tier.TierType.valueOf(tier.getTierTypeString()));
        tier.setCode("TIER-" + helperService.generateRandomCode());

        return this.tierRepository.save(tier);
    }

    @Override
    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public void delete(Long id) {
        this.tierRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public void delete(Tier tier) {
        this.tierRepository.delete(tier);
    }

    @Override
    public Tier findByCode(String code) {
        return this.tierRepository.findByCode(code);
    }
}
