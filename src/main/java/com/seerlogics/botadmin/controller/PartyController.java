package com.seerlogics.botadmin.controller;

import com.seerlogics.botadmin.model.Party;
import com.seerlogics.botadmin.service.PartyService;
import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.lingoace.validation.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by bkane on 11/3/18.
 */
@RestController
@RequestMapping(value = "/api/v1/party")
public class PartyController extends BaseController implements CrudController<Party> {
    @Autowired
    private PartyService partyService;

    @PostMapping(value = {"", "/",})
    public Boolean save(@Validate("validatePersonRule") @RequestBody Party object) {
        this.partyService.save(object);
        return true;
    }

    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<Party> getAll() {
        return this.partyService.getAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public Party getById(@PathVariable("id") Long id) {
        return this.partyService.getSingle(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delete(@PathVariable("id") Long id) {
        this.partyService.delete(id);
        return true;
    }

    @GetMapping(value = "/init/{type}")
    @ResponseBody
    public Party initParty(@PathVariable("type") String type) {
        return this.partyService.initParty(type);
    }
}
