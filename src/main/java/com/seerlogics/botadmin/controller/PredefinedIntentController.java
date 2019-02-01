package com.seerlogics.botadmin.controller;

import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.PredefinedIntentUtterances;
import com.seerlogics.botadmin.service.CategoryService;
import com.seerlogics.botadmin.service.PredefinedIntentService;
import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
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
@RequestMapping(value = "/api/v1/predefined-intent")
public class PredefinedIntentController extends BaseController implements CrudController<PredefinedIntentUtterances> {
    @Autowired
    private PredefinedIntentService predefinedIntentService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@RequestBody PredefinedIntentUtterances utterances) {
        this.predefinedIntentService.save(utterances);
        return true;
    }

    @PostMapping(value = {"/save-all"})
    @ResponseBody
    public Boolean save(@RequestBody List<PredefinedIntentUtterances> intentUtterances) {
        this.predefinedIntentService.saveAll(intentUtterances);
        return true;
    }

    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<PredefinedIntentUtterances> getAll() {
        return this.predefinedIntentService.getAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public PredefinedIntentUtterances getById(@PathVariable("id") Long id) {
        PredefinedIntentUtterances predefinedIntentUtterances = this.predefinedIntentService.getSingle(id);
        predefinedIntentUtterances.getReferenceData().put("categories", categoryService.getAll());
        return predefinedIntentUtterances;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delete(@PathVariable("id") Long id) {
        this.predefinedIntentService.delete(id);
        return true;
    }

    @GetMapping(value = "/init")
    @ResponseBody
    public PredefinedIntentUtterances init() {
        PredefinedIntentUtterances predefinedIntentUtterances = new PredefinedIntentUtterances();
        predefinedIntentUtterances.getReferenceData().put("categories", categoryService.getAll());
        return predefinedIntentUtterances;
    }

    @GetMapping(value = "/search/{category}")
    @ResponseBody
    public List<PredefinedIntentUtterances> searchByCat(@PathVariable("category") String category) {
        return predefinedIntentService.findIntentsByCategory(category);
    }

    @PostMapping("/upload")
    public Boolean uploadIntentsFromFile(@RequestPart("intentsData") MultipartFile file,
                                         @RequestPart("category") String categoryCode) {
        if (!file.isEmpty()) {
            try {
                String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                String[] rows = fileContent.split("\n");
                List<PredefinedIntentUtterances> predefinedIntentUtterancesList = new ArrayList<>();
                Collection<Category> categories = this.categoryService.getAll();
                Category category = categories.stream().filter(categoryOne -> categoryCode.equals(categoryOne.getCode())).findAny().orElse(null);
                for (String row : rows) {
                    String[] cols = row.split(" ", 2);
                    LOGGER.debug(String.format("intent: %s, utterance: %s", cols[0], cols[1]));
                    PredefinedIntentUtterances predefinedIntentUtterances = new PredefinedIntentUtterances();
                    predefinedIntentUtterances.setCategory(category);
                    predefinedIntentUtterances.setIntent(cols[0].trim());
                    predefinedIntentUtterances.setUtterance(cols[1].trim());
                    predefinedIntentUtterancesList.add(predefinedIntentUtterances);
                }
                this.predefinedIntentService.saveAll(predefinedIntentUtterancesList);
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
        return predefinedIntentService.initSearchIntentsCriteria();
    }

    @PostMapping("/search")
    @ResponseBody
    public List<PredefinedIntentUtterances> searchIntents(@Validate("validateSearchIntentRule")
                                                              @RequestBody SearchIntents searchIntents) {
        return predefinedIntentService.findIntentsAndUtterances(searchIntents);
    }
}
