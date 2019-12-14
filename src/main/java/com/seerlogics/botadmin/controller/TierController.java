package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.TierService;
import com.seerlogics.commons.model.Tier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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

    @PostMapping(value = {"", "/"})
    public Tier saveTier(@RequestBody Tier tier) {
        return this.tierService.save(tier);
    }

    @DeleteMapping(value = "/{tierId}")
    public ResponseEntity deleteTier(@PathVariable Long tierId) {
        this.tierService.delete(tierId);
        return returnSuccessResponse();
    }

    @GetMapping(value = "/{tierId}")
    public Tier getTier(@PathVariable Long tierId) {
        return this.tierService.getSingle(tierId);
    }

    @GetMapping(value = "/getAll")
    public Collection<Tier> getAllTiers() {
        return this.tierService.getAll();
    }
}
