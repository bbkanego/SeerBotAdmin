package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Role;
import com.seerlogics.commons.repository.RoleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@Service
@Transactional("botAdminTransactionManager")
public class RoleService extends BaseServiceImpl<Role> {

    private final RoleRepository roleRepository;
    private final PolicyService policyService;

    public RoleService(RoleRepository roleRepository, PolicyService policyService) {
        this.roleRepository = roleRepository;
        this.policyService = policyService;
    }

    @Override
    public Collection<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role getSingle(Long id) {
        Role role = roleRepository.getOne(id);
        addReferenceData(role);
        return role;
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    @Override
    public Role save(Role object) {
        return roleRepository.save(object);
    }

    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role findByCode(String code) {
        return roleRepository.findByCode(code);
    }

    public Role initModel() {
        Role role = new Role();
        addReferenceData(role);
        return role;
    }

    private void addReferenceData(Role role) {
        role.getReferenceData().put("policies", policyService.getAll());
    }
}
