package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.PolicyService;
import com.seerlogics.commons.model.Policy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bkane on 11/3/18.
 */
@RestController
@RequestMapping(value = "/api/v1/policy")
public class PolicyController extends BaseController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping(value = {"", "/",})
    public ResponseEntity save(@RequestBody Policy policy) {
        return returnObjectResponse(policyService.save(policy));
    }

    @GetMapping(value = {"", "/",})
    public ResponseEntity getAll() {
        return returnObjectResponse(this.policyService.getAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getById(@PathVariable("id") Long id) {
        return returnObjectResponse(this.policyService.getSingle(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        this.policyService.delete(id);
        return returnSuccessResponse();
    }

    @GetMapping(value = "/init")
    public ResponseEntity initModel() {
        return returnObjectResponse(policyService.initModel());
    }
}

