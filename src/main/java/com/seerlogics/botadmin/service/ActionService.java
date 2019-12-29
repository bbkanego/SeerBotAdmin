package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.CommonConstants;
import com.seerlogics.commons.model.Action;
import com.seerlogics.commons.repository.ActionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Transactional("botAdminTransactionManager")
@Service(value = "actionService")
@PreAuthorize(CommonConstants.HAS_UBER_ADMIN_ROLE)
public class ActionService extends BaseServiceImpl<Action> {

    private final ActionRepository actionRepository;

    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Override
    public Collection<Action> getAll() {
        return this.actionRepository.findAll();
    }

    @Override
    public Action getSingle(Long id) {
        return this.actionRepository.getOne(id);
    }

    @Override
    public Action save(Action action) {
        if (StringUtils.isBlank(action.getCode())) {
            action.setCode("ACT-" + UUID.randomUUID());
        }
        return this.actionRepository.save(action);
    }

    @Override
    public void delete(Long id) {
        this.actionRepository.deleteById(id);
    }

    public Action initModel() {
        return new Action();
    }

    private void addReferenceData(Action Action) {

    }
}