package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.ActionService;
import com.seerlogics.commons.model.Action;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bkane on 11/3/18.
 */
@RestController
@RequestMapping(value = "/api/v1/action")
public class ActionController extends BaseController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @PostMapping(value = {"", "/",})
    public ResponseEntity save(@RequestBody Action action) {
        return returnObjectResponse(this.actionService.save(action));
    }

    @GetMapping(value = {"", "/",})
    public ResponseEntity getAll() {
        return returnObjectResponse(this.actionService.getAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getById(@PathVariable("id") Long id) {
        return returnObjectResponse(this.actionService.getSingle(id));
    }

    @GetMapping(value = "/init")
    public ResponseEntity initModel() {
        return returnObjectResponse(actionService.initModel());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        this.actionService.delete(id);
        return returnSuccessResponse();
    }
}