package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.service.AccountService;
import com.seerlogics.botadmin.service.CategoryService;
import com.seerlogics.botadmin.service.IntentService;
import com.seerlogics.botadmin.service.TransactionService;
import com.seerlogics.commons.dto.CopyIntents;
import com.seerlogics.commons.dto.ReTrainBot;
import com.seerlogics.commons.dto.SearchIntents;
import com.seerlogics.commons.model.Intent;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE;

/**
 * Created by bkane on 11/11/18.
 */
@RestController
@RequestMapping(value = "/api/v1/custom-intent")
@PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
public class CustomIntentController extends BaseController implements CrudController<Intent> {
    private final IntentService customIntentService;

    private final AccountService accountService;

    private final TransactionService transactionService;

    public CustomIntentController(IntentService customIntentService, CategoryService categoryService,
                                  AccountService accountService, TransactionService transactionService) {
        this.customIntentService = customIntentService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping(value = "/copy-standard-intents/{catCode}")
    public Collection<Intent> copyStandardIntents(@PathVariable("catCode") String catCode) {
        return this.customIntentService.copyPredefinedIntents(catCode);
    }

    @PostMapping(value = {"", "/",})
    public Boolean save(@Validate("validateIntentRule") @RequestBody Intent intent) {
        this.customIntentService.save(intent);
        return true;
    }

    @PostMapping(value = {"/save-all"})
    public Boolean save(@RequestBody List<Intent> intentUtterances) {
        this.customIntentService.saveAll(intentUtterances);
        return true;
    }

    @GetMapping(value = {"", "/",})
    public Collection<Intent> getAll() {
        return this.customIntentService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Intent getById(@PathVariable("id") Long id) {
        return this.customIntentService.getSingle(id);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean delete(@PathVariable("id") Long id) {
        this.customIntentService.delete(id);
        return true;
    }

    @GetMapping(value = "/init")
    public Intent init() {
        return this.customIntentService.initCustomIntent();
    }

    @GetMapping(value = "/search/{category}")
    public List<Intent> searchByCat(@PathVariable("category") String category) {
        return customIntentService.findAllByCategoryCodeAndOwner(category);
    }

    @PostMapping(value = "/upload")
    public Boolean uploadIntentsFromFile(@RequestPart("intentsData") MultipartFile file,
                                         @RequestPart("category") String categoryCode) {
        return this.customIntentService.uploadIntentsFromFile(file, categoryCode, Intent.INTENT_TYPE.CUSTOM);
    }

    @GetMapping("/search/init")
    public SearchIntents initSearchIntents() {
        return customIntentService.initSearchIntentsCriteria(Intent.INTENT_TYPE.CUSTOM.name());
    }

    @PostMapping("/search")
    public List<Intent> searchIntents(@Validate("validateSearchIntentRule")
                                      @RequestBody SearchIntents searchIntents) {
        searchIntents.setOwnerAccount(accountService.getAuthenticatedUser());
        return customIntentService.findIntentsAndUtterances(searchIntents);
    }

    @DeleteMapping("/delete-all/{category}")
    public ResponseEntity deleteAllIntentsByCategory(@PathVariable("category") String category) {
        List<Intent> intents = customIntentService.findAllByCategoryCodeAndOwner(category);
        this.customIntentService.deleteAll(intents);
        return returnSuccessResponse();
    }

    @PostMapping("/associateIntents")
    public ReTrainBot associateAndRetrain(@RequestBody ReTrainBot reTrainBot) {
        return this.transactionService.associateUtteranceToIntents(reTrainBot);
    }

    @PostMapping("/copyIntents")
    public ResponseEntity copyIntentsFromCategory(@RequestBody CopyIntents copyIntents) {
        this.customIntentService.copyIntentsFromCategory(copyIntents);
        return returnSuccessResponse();
    }
}
