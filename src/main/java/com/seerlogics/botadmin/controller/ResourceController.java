package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.ResourceService;
import com.seerlogics.commons.model.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/resource")
public class ResourceController extends BaseController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(value = {"", "/",})
    public Resource save(@RequestBody Resource resource) {
        return this.resourceService.save(resource);
    }

    @GetMapping(value = {"", "/",})
    public Collection<Resource> getAll() {
        return this.resourceService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Resource getById(@PathVariable("id") Long id) {
        return this.resourceService.getSingle(id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        this.resourceService.delete(id);
        return returnSuccessResponse();
    }

    @GetMapping(value = "/init")
    public Resource initModel() {
        return resourceService.initModel();
    }
}

