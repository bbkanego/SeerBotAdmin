package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Tier;
import com.seerlogics.commons.repository.TierRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TierService extends BaseServiceImpl<Tier> {
    private final TierRepository tierRepository;

    public TierService(TierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    public Tier initTier() {
        return new Tier();
    }

    @Override
    public Collection<Tier> getAll() {
        return this.tierRepository.findAll();
    }

    @Override
    public Tier getSingle(Long id) {
        return this.tierRepository.getOne(id);
    }

    @Override
    public Tier save(Tier object) {
        return this.tierRepository.save(object);
    }

    @Override
    public void delete(Long id) {
        this.tierRepository.deleteById(id);
    }

    public void delete(Tier tier) {
        this.tierRepository.delete(tier);
    }

    @Override
    public Tier findByCode(String code) {
        return this.tierRepository.findByCode(code);
    }
}
