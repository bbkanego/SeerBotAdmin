package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.service.IntentService;
import com.seerlogics.commons.CommonConstants;
import com.seerlogics.commons.dto.SearchIntents;
import com.seerlogics.commons.model.Intent;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 11/11/18.
 */
@RestController
@RequestMapping(value = "/api/v1/predefined-intent")
@PreAuthorize(CommonConstants.HAS_UBER_ADMIN_ROLE)
public class PredefinedIntentController extends BaseController implements CrudController<Intent> {

    private final IntentService predefinedIntentService;

    public PredefinedIntentController(IntentService predefinedIntentService) {
        this.predefinedIntentService = predefinedIntentService;
    }

    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@RequestBody Intent utterances) {
        this.predefinedIntentService.save(utterances);
        return true;
    }

    @PostMapping(value = {"/save-all"})
    @ResponseBody
    public Boolean save(@RequestBody List<Intent> intentUtterances) {
        this.predefinedIntentService.saveAll(intentUtterances);
        return true;
    }

    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<Intent> getAll() {
        return this.predefinedIntentService.getAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public Intent getById(@PathVariable("id") Long id) {
        return this.predefinedIntentService.getSingle(id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delete(@PathVariable("id") Long id) {
        this.predefinedIntentService.delete(id);
        return true;
    }

    @GetMapping(value = "/init")
    @ResponseBody
    public Intent init() {
        return predefinedIntentService.initPredefinedIntent();
    }

    @GetMapping(value = "/search/{category}")
    @ResponseBody
    public List<Intent> searchByCat(@PathVariable("category") String category) {
        return predefinedIntentService.findAllByCategoryCodeAndOwner(category);
    }

    @PostMapping(value = "/upload")
    public Boolean uploadIntentsFromFile(@RequestPart("intentsData") MultipartFile file,
                                         @RequestPart("category") String categoryCode) {
        return predefinedIntentService.uploadIntentsFromFile(file, categoryCode, Intent.INTENT_TYPE.PREDEFINED);
    }

    @GetMapping("/search/init")
    @ResponseBody
    public SearchIntents initSearchIntents() {
        return predefinedIntentService.initSearchIntentsCriteria(Intent.INTENT_TYPE.PREDEFINED.name());
    }

    @PostMapping("/search")
    @ResponseBody
    public List<Intent> searchIntents(@Validate("validateSearchIntentRule")
                                      @RequestBody SearchIntents searchIntents) {
        return predefinedIntentService.findIntentsAndUtterances(searchIntents);
    }

    @GetMapping("/export/csv")
    @ResponseBody
    public String exportIntentsAsCSV(@RequestParam("ccode") String categoryCode,
                                     @RequestParam("int") String intentType, HttpServletResponse response) {
        List<List<String>> intents = predefinedIntentService.exportIntents(categoryCode, intentType);

        response.setContentType("text/csv");
        response.setStatus(200);
        String filename = "intents.csv";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");
        StringBuilder stringBuilder = new StringBuilder();
        for (List<String> intent : intents) {
            for (String item : intent) {
                stringBuilder.append(item);
            }
        }
        return stringBuilder.toString();
    }
}
