package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.RoleService;
import com.seerlogics.commons.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@RestController
@RequestMapping(value = "/api/v1/role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    @PostMapping(value = {"", "/",})
    public ResponseEntity<String> save(@RequestBody Role role) {
        this.roleService.save(role);
        return returnSuccessResponse();
    }

    @GetMapping(value = {"", "/",})
    public ResponseEntity<Collection<Role>> getAll() {
        return new ResponseEntity<>(this.roleService.getAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Role> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(this.roleService.getSingle(id), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        this.roleService.delete(id);
        return returnSuccessResponse();
    }
}
