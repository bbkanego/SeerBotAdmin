package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Policy;
import com.seerlogics.commons.model.Statement;
import com.seerlogics.commons.repository.ActionRepository;
import com.seerlogics.commons.repository.PolicyRepository;
import com.seerlogics.commons.repository.ResourceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional("botAdminTransactionManager")
@Service(value = "policyService")
@PreAuthorize("hasAnyRole('UBER_ADMIN')")
public class PolicyService extends BaseServiceImpl<Policy> {

    private final PolicyRepository policyRepository;
    private final ResourceRepository resourceRepository;
    private final ActionRepository actionRepository;

    public PolicyService(PolicyRepository policyRepository, ResourceRepository resourceRepository, ActionRepository actionRepository) {
        this.policyRepository = policyRepository;
        this.resourceRepository = resourceRepository;
        this.actionRepository = actionRepository;
    }

    @Override
    public Collection<Policy> getAll() {
        return policyRepository.findAll();
    }

    @Override
    public Policy getSingle(Long id) {
        Policy policy = policyRepository.getOne(id);
        addReferenceData(policy);
        return policy;
    }

    @Override
    public Policy save(Policy policy) {
        policy.getStatements().forEach((statement -> {
            statement.setOwner(policy);
            String uuid = UUID.randomUUID().toString();
            statement.setCode("STMT-" + uuid);
            statement.setName("Name-" + uuid);
            statement.setDescription("Description-" + uuid);
        }));
        String uuid = UUID.randomUUID().toString();
        policy.setCode("PLC-" + uuid);
        return policyRepository.save(policy);
    }

    @Override
    public void delete(Long id) {
        policyRepository.deleteById(id);
    }

    public Policy initModel() {
        Policy policy = new Policy();
        Statement statement = new Statement();
        policy.getStatements().add(statement);
        addReferenceData(policy);
        return policy;
    }

    private void addReferenceData(Policy policy) {
        policy.getReferenceData().put("resources", resourceRepository.findAll());
        policy.getReferenceData().put("actions", actionRepository.findAll());

        // create effects drop down.
        Statement.Effect[] effects = Statement.Effect.values();
        List<Map<String, String>> effectsList = new ArrayList<>();
        for (Statement.Effect effect : effects) {
            Map<String, String> effectsDD = new HashMap<>();
            effectsDD.put("code", effect.name());
            effectsDD.put("name", effect.getEffectString());
            effectsList.add(effectsDD);
        }
        policy.getReferenceData().put("effects", effectsList);
    }
}