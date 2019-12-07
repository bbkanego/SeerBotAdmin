package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.TierService;
import com.seerlogics.commons.model.Tier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/tier")
@PreAuthorize("hasAnyRole('UBER_ADMIN')")
public class TierController extends BaseController {

    private final TierService tierService;

    public TierController(TierService tierService) {
        this.tierService = tierService;
    }

    @GetMapping(value = "/init")
    public Tier initTier() {
        return this.tierService.initTier();
    }
}
