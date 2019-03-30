package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Role;
import com.seerlogics.commons.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@Service
@Transactional
public class RoleService extends BaseServiceImpl<Role> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Collection<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role getSingle(Long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public Role save(Role object) {
        return roleRepository.save(object);
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role findByCode(String code) {
        return roleRepository.findByCode(code);
    }
}
