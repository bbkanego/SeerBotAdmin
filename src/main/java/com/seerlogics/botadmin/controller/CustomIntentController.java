package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.Intent;
import com.seerlogics.botadmin.service.CategoryService;
import com.seerlogics.botadmin.service.IntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 11/11/18.
 */
@RestController
@RequestMapping(value = "/api/v1/custom-intent")
public class CustomIntentController extends BaseController implements CrudController<Intent> {
    @Autowired
    private IntentService customIntentService;

    @Autowired
    private CategoryService categoryService;

    /**
     * This method allows you to select categtory and copy standard intents to your custom intent table.
     */
    @ResponseBody
    public Collection<Category> initCopyStandardIntents() {
        return categoryService.getAll();
    }

    @GetMapping(value = "/copy-standard-intents/{catCode}")
    public Collection<Intent> copyStandardIntents(@PathVariable("catCode") String catCode) {
        return this.customIntentService.copyPredefinedIntents(catCode);
    }

    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@RequestBody Intent intent) {
        this.customIntentService.save(intent);
        return true;
    }

    @PostMapping(value = {"/save-all"})
    @ResponseBody
    public Boolean save(@RequestBody List<Intent> intentUtterances) {
        this.customIntentService.saveAll(intentUtterances);
        return true;
    }

    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<Intent> getAll() {
        return this.customIntentService.getAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public Intent getById(@PathVariable("id") Long id) {
        return this.customIntentService.getSingle(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delete(@PathVariable("id") Long id) {
        this.customIntentService.delete(id);
        return true;
    }

    @GetMapping(value = "/init")
    @ResponseBody
    public Intent init() {
        return this.customIntentService.initCustomIntent();
    }

    @GetMapping(value = "/search/{category}")
    @ResponseBody
    public List<Intent> searchByCat(@PathVariable("category") String category) {
        return customIntentService.findIntentsByCategory(category);
    }

    @PostMapping(value = "/upload")
    public Boolean uploadIntentsFromFile(@RequestPart("intentsData") MultipartFile file,
                                         @RequestPart("category") String categoryCode) {
        return this.customIntentService.uploadIntentsFromFile(file, categoryCode, Intent.INTENT_TYPE.CUSTOM);
    }

    @GetMapping("/search/init")
    @ResponseBody
    public SearchIntents initSearchIntents() {
        return customIntentService.initSearchIntentsCriteria();
    }

    @PostMapping("/search")
    @ResponseBody
    public List<Intent> searchIntents(@Validate("validateSearchIntentRule")
                                                     @RequestBody SearchIntents searchIntents) {
        return customIntentService.findIntentsAndUtterances(searchIntents);
    }
}
