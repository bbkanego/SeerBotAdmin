package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.seerlogics.botadmin.service.LanguageService;
import com.seerlogics.commons.model.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 11/11/18.
 */
@RestController
@RequestMapping(value = "/api/v1/lang")
public class LanguageController extends BaseController implements CrudController<Language> {
    @Autowired
    private LanguageService languageService;

    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@RequestBody Language language) {
        this.languageService.save(language);
        return true;
    }

    @PostMapping(value = {"/save-all"})
    @ResponseBody
    public Boolean save(@RequestBody List<Language> languages) {
        this.languageService.saveAll(languages);
        return true;
    }

    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<Language> getAll() {
        return this.languageService.getAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public Language getById(@PathVariable("id") Long id) {
        return this.languageService.getSingle(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delete(@PathVariable("id") Long id) {
        this.languageService.delete(id);
        return true;
    }
}
