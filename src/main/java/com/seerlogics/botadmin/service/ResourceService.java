package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Resource;
import com.seerlogics.commons.repository.ResourceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Transactional("botAdminTransactionManager")
@Service(value = "resourceService")
@PreAuthorize("hasAnyRole('UBER_ADMIN')")
public class ResourceService extends BaseServiceImpl<Resource> {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Collection<Resource> getAll() {
        return this.resourceRepository.findAll();
    }

    @Override
    public Resource getSingle(Long id) {
        return this.resourceRepository.getOne(id);
    }

    @Override
    public Resource save(Resource resource) {
        if (StringUtils.isBlank(resource.getCode())) {
            resource.setCode("RSC-" + UUID.randomUUID());
        }
        return this.resourceRepository.save(resource);
    }

    @Override
    public void delete(Long id) {
        this.resourceRepository.deleteById(id);
    }

    public Resource initModel() {
        return new Resource();
    }

    private void addReferenceData(Resource Resource) {

    }
}