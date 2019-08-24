package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.RoleService;
import com.seerlogics.commons.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@RestController
@RequestMapping(value = "/api/v1/role")
public class RoleController extends BaseController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(value = {"", "/",})
    public ResponseEntity<String> save(@RequestBody Role role) {
        this.roleService.save(role);
        return returnSuccessResponse();
    }

    @GetMapping(value = {"", "/",})
    public Collection<Role> getAll() {
        return this.roleService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Role getById(@PathVariable("id") Long id) {
        return this.roleService.getSingle(id);
    }

    @GetMapping(value = "/init")
    public Role initModel() {
        return this.roleService.initModel();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        this.roleService.delete(id);
        return returnSuccessResponse();
    }
}
