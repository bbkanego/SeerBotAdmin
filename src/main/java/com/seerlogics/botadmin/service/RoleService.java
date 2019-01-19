package com.seerlogics.botadmin.service;

import com.seerlogics.botadmin.model.Role;
import com.seerlogics.botadmin.repository.RoleRepository;
import com.lingoace.spring.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@Service
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
}
