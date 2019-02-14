package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.CustomIntentUtterance;
import com.seerlogics.botadmin.service.AccountService;
import com.seerlogics.botadmin.service.CategoryService;
import com.seerlogics.botadmin.service.CustomIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 11/11/18.
 */
@RestController
@RequestMapping(value = "/api/v1/custom-intent")
public class CustomIntentController extends BaseController implements CrudController<CustomIntentUtterance> {
    @Autowired
    private CustomIntentService customIntentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountService accountService;

    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@RequestBody CustomIntentUtterance utterances) {
        this.customIntentService.save(utterances);
        return true;
    }

    @PostMapping(value = {"/save-all"})
    @ResponseBody
    public Boolean save(@RequestBody List<CustomIntentUtterance> intentUtterances) {
        this.customIntentService.saveAll(intentUtterances);
        return true;
    }

    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<CustomIntentUtterance> getAll() {
        return this.customIntentService.getAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public CustomIntentUtterance getById(@PathVariable("id") Long id) {
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
    public CustomIntentUtterance init() {
        return this.customIntentService.initCustomIntentUtterance();
    }

    @GetMapping(value = "/search/{category}")
    @ResponseBody
    public List<CustomIntentUtterance> searchByCat(@PathVariable("category") String category) {
        return customIntentService.findIntentsByCategory(category);
    }

    @PostMapping(value = "/upload")
    public Boolean uploadIntentsFromFile(@RequestPart("intentsData") MultipartFile file,
                                         @RequestPart("category") String categoryCode) {
        if (!file.isEmpty()) {
            try {
                String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                String[] rows = fileContent.split("\n");
                List<CustomIntentUtterance> customIntentUtteranceList = new ArrayList<>();
                Collection<Category> categories = this.categoryService.getAll();
                Category category = categories.stream().filter(categoryOne -> categoryCode.equals(categoryOne.getCode())).findAny().orElse(null);
                for (String row : rows) {
                    String[] cols = row.split(" ", 2);
                    LOGGER.debug(String.format("intent: %s, utterance: %s", cols[0], cols[1]));
                    CustomIntentUtterance customIntentUtterance = new CustomIntentUtterance();
                    customIntentUtterance.setCategory(category);
                    customIntentUtterance.setIntent(cols[0].trim());
                    customIntentUtterance.setUtterance(cols[1].trim());
                    customIntentUtterance.setOwner(this.accountService.getAuthenticatedUser());
                    customIntentUtteranceList.add(customIntentUtterance);
                }
                this.customIntentService.saveAll(customIntentUtteranceList);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    @GetMapping("/search/init")
    @ResponseBody
    public SearchIntents initSearchIntents() {
        return customIntentService.initSearchIntentsCriteria();
    }

    @PostMapping("/search")
    @ResponseBody
    public List<CustomIntentUtterance> searchIntents(@Validate("validateSearchIntentRule")
                                                              @RequestBody SearchIntents searchIntents) {
        return customIntentService.findIntentsAndUtterances(searchIntents);
    }
}
