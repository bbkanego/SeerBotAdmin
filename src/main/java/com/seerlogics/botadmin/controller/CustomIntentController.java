package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.service.CategoryService;
import com.seerlogics.botadmin.service.IntentService;
import com.seerlogics.commons.dto.SearchIntents;
import com.seerlogics.commons.model.Category;
import com.seerlogics.commons.model.Intent;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final IntentService customIntentService;

    private final CategoryService categoryService;

    public CustomIntentController(IntentService customIntentService, CategoryService categoryService) {
        this.customIntentService = customIntentService;
        this.categoryService = categoryService;
    }

    /**
     * This method allows you to select categtory and copy standard intents to your custom intent table.
     */
    @ResponseBody
    public Collection<Category> initCopyStandardIntents() {
        return categoryService.getAll();
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/copy-standard-intents/{catCode}")
    public Collection<Intent> copyStandardIntents(@PathVariable("catCode") String catCode) {
        return this.customIntentService.copyPredefinedIntents(catCode);
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@Validate("validateIntentRule") @RequestBody Intent intent) {
        this.customIntentService.save(intent);
        return true;
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @PostMapping(value = {"/save-all"})
    @ResponseBody
    public Boolean save(@RequestBody List<Intent> intentUtterances) {
        this.customIntentService.saveAll(intentUtterances);
        return true;
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<Intent> getAll() {
        return this.customIntentService.getAll();
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/{id}")
    @ResponseBody
    public Intent getById(@PathVariable("id") Long id) {
        return this.customIntentService.getSingle(id);
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delete(@PathVariable("id") Long id) {
        this.customIntentService.delete(id);
        return true;
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/init")
    @ResponseBody
    public Intent init() {
        return this.customIntentService.initCustomIntent();
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping(value = "/search/{category}")
    @ResponseBody
    public List<Intent> searchByCat(@PathVariable("category") String category) {
        return customIntentService.findIntentsByCategory(category);
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @PostMapping(value = "/upload")
    public Boolean uploadIntentsFromFile(@RequestPart("intentsData") MultipartFile file,
                                         @RequestPart("category") String categoryCode) {
        return this.customIntentService.uploadIntentsFromFile(file, categoryCode, Intent.INTENT_TYPE.CUSTOM);
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @GetMapping("/search/init")
    @ResponseBody
    public SearchIntents initSearchIntents() {
        return customIntentService.initSearchIntentsCriteria(Intent.INTENT_TYPE.CUSTOM.name());
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    @PostMapping("/search")
    @ResponseBody
    public List<Intent> searchIntents(@Validate("validateSearchIntentRule")
                                      @RequestBody SearchIntents searchIntents) {
        return customIntentService.findIntentsAndUtterances(searchIntents);
    }
}
