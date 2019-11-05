package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.StatusService;
import com.seerlogics.commons.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 11/3/18.
 */
@RestController
@RequestMapping(value = "/api/v1/status")
public class StatusController extends BaseController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping(value = {"", "/",})
    public ResponseEntity save(@RequestBody Status role) {
        this.statusService.save(role);
        return returnSuccessResponse();
    }

    @GetMapping(value = {"", "/",})
    public ResponseEntity<Collection<Status>> getAll() {
        return new ResponseEntity<>(this.statusService.getAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Status> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(this.statusService.getSingle(id), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        this.statusService.delete(id);
        return returnSuccessResponse();
    }

    @PostMapping(value = {"/save-all"})
    @ResponseBody
    public Boolean save(@RequestBody List<Status> languages) {
        this.statusService.saveAll(languages);
        return true;
    }
}
