package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Status;
import com.seerlogics.commons.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 11/3/18.
 */
@Service
@Transactional("botAdminTransactionManager")
public class StatusService extends BaseServiceImpl<Status> {

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public Collection<Status> getAll() {
        return statusRepository.findAll();
    }

    @Override
    public Status getSingle(Long id) {
        return statusRepository.getOne(id);
    }

    @Override
    public Status save(Status object) {
        return statusRepository.save(object);
    }

    @Override
    public void delete(Long id) {
        statusRepository.deleteById(id);
    }

    @Override
    public List<Status> saveAll(Collection<Status> languages) {
        return statusRepository.saveAll(languages);
    }

    @Override
    public Status findByCode(String code) {
        return statusRepository.findByCode(code);
    }
}
