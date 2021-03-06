package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.lingoace.validation.ValidationException;
import com.lingoace.validation.ValidationResult;
import com.seerlogics.botadmin.exception.ErrorCodes;
import com.seerlogics.commons.CommonConstants;
import com.seerlogics.commons.CommonUtils;
import com.seerlogics.commons.dto.CopyIntents;
import com.seerlogics.commons.dto.SearchIntents;
import com.seerlogics.commons.exception.BaseRuntimeException;
import com.seerlogics.commons.model.*;
import com.seerlogics.commons.repository.IntentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by bkane on 11/3/18.
 */
@Service
@Transactional("botAdminTransactionManager")
@PreAuthorize(CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
public class IntentService extends BaseServiceImpl<Intent> {

    private final IntentRepository intentRepository;

    private final CategoryService categoryService;

    private final LanguageService languageService;

    private final AccountService accountService;

    private final HelperService helperService;

    public IntentService(IntentRepository intentRepository,
                         CategoryService categoryService, LanguageService languageService,
                         AccountService accountService, HelperService helperService) {
        this.intentRepository = intentRepository;
        this.categoryService = categoryService;
        this.languageService = languageService;
        this.accountService = accountService;
        this.helperService = helperService;
    }

    @Override
    public Collection<Intent> getAll() {
        Account currentAccount = this.accountService.getAuthenticatedUser();
        if (HelperService.isAllowedFullAccess(currentAccount)) {
            return intentRepository.findAll();
        } else {
            // get intents based on the owner
            return intentRepository.findAllByOwnerEquals(currentAccount);
        }
    }

    @Override
    public Intent getSingle(Long id) {
        Intent intent = intentRepository.getOne(id);
        addReferenceData(intent);
        return intent;
    }

    @Override
    public Intent save(Intent intent) {
        return saveIntentLocal(intent, this.accountService.getAuthenticatedUser());

    }

    private Intent saveIntentLocal(Intent intent, Account ownerAccount) {
        // get existing intents for the category
        List<Intent> existingIntents =
                this.intentRepository.findAllByCategoryCodeAndOwnerEquals(intent.getCategory().getCode(),
                        ownerAccount);

        // add the new intent to the existing intents to check if its duplicate
        if (intent.getId() == null) {
            existingIntents.add(intent);
            checkForDuplicateIntent(existingIntents);
        }

        // check for duplicate utterance
        checkForDuplicateIntentUtterance(intent.getUtterances());

        List<IntentResponse> newResponses = new ArrayList<>(intent.getResponses());
        intent.getResponses().clear();
        intent.setOwner(ownerAccount);
        intent.getResponses().addAll(newResponses);
        Intent mayBeIntent = intent.getMayBeIntent();
        if (mayBeIntent != null && mayBeIntent.getId() == null) {
            mayBeIntent.setOwner(intent.getOwner());
            mayBeIntent.setIntent(CommonConstants.MAY_BE + intent.getIntent());
            mayBeIntent.setIntentType(Intent.INTENT_TYPE.MAYBE.name());
            mayBeIntent.setCategory(intent.getCategory());
            IntentResponse intentResponses = (IntentResponse) mayBeIntent.getResponses().toArray()[0];
            intentResponses.setResponseType(IntentResponse.RESPONSE_TYPE.MAYBE.name());
        }
        return intentRepository.save(intent);
    }

    @Override
    public void delete(Long id) {
        intentRepository.deleteById(id);
    }

    public void deleteAll(List<Intent> intents) {
        this.intentRepository.deleteAll(intents);
    }

    @Override
    @PreAuthorize(CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public List<Intent> saveAll(Collection<Intent> intentsAndUtternaces) {

        checkForDuplicateIntent(intentsAndUtternaces);

        for (Intent intent : intentsAndUtternaces) {
            checkForDuplicateIntentUtterance(intent.getUtterances());
        }

        return intentRepository.saveAll(intentsAndUtternaces);
    }

    private void checkForDuplicateIntentUtterance(Collection<IntentUtterance> intentUtterances) {
        // check for any duplicate intents.
        Set<String> setToCheckDuplicates = new HashSet<>();
        List<String> duplicateUtteranceList = new ArrayList<>();
        for (IntentUtterance intentUtterance : intentUtterances) {
            // check for duplicate utterance in the same Intent
            if (!setToCheckDuplicates.add(intentUtterance.getUtterance())) {
                duplicateUtteranceList.add(intentUtterance.getUtterance());
            }
        }

        if (duplicateUtteranceList.size() > 0) {
            ValidationResult validationResult = new ValidationResult();
            ValidationException validationException = new ValidationException("DuplicateUtterances", validationResult);
            for (String utterance : duplicateUtteranceList) {
                validationResult.addPageLevelError(this.helperService.getMessage(
                        "message.intents.utterance.already.added",
                        new String[]{utterance}));
            }
            throw validationException;
        }
    }

    private void checkForDuplicateIntent(Collection<Intent> intentsAndUtterances) {
        // check for any duplicate intents.
        Set<String> setToCheckDuplicates = new HashSet<>();
        List<String> duplicateIntents = new ArrayList<>();
        // check for duplicate Intents
        for (Intent intent : intentsAndUtterances) {
            if (!setToCheckDuplicates.add(intent.getIntent())) {
                duplicateIntents.add(intent.getIntent());
            }
        }

        if (duplicateIntents.size() > 0) {
            List<String> errorMessages = new ArrayList<>();
            for (String utterance : duplicateIntents) {
                errorMessages.add(this.helperService.getMessage("message.intents.already.added",
                        new String[]{utterance}));
            }
            CommonUtils.showPageLevelValidationErrors("DuplicateIntents", errorMessages);
        }
    }

    public List<Intent> findAllByCategoryCodeAndOwner(String catCode) {
        return intentRepository.findAllByCategoryCodeAndOwnerEquals(catCode, this.accountService.getAuthenticatedUser());
    }

    public List<Intent> findIntentsByCategoryAndType(List<String> catCode, String intentType) {
        return intentRepository.findIntentsByCodeAndType(catCode, intentType);
    }

    public List<Intent> findIntentsByCategoryTypeAndOwner(String catCode, String intentType) {
        return intentRepository.findIntentsByCodeTypeAndOwner(catCode, intentType, accountService.getAuthenticatedUser());
    }

    public SearchIntents initSearchIntentsCriteria(String type) {
        SearchIntents searchIntents = new SearchIntents();
        searchIntents.setIntentType(type);
        searchIntents.getReferenceData().put("categories", categoryService.findFilteredCategoriesAllForSelection());
        return searchIntents;
    }

    public List<Intent> findIntentsAndUtterances(SearchIntents searchIntents) {
        searchIntents.setOwnerAccount(this.accountService.getAuthenticatedUser());
        return intentRepository.findIntentsAndUtterances(searchIntents);
    }

    private void addReferenceData(Intent intent) {
        intent.getReferenceData().put("categories", categoryService.findFilteredCategoriesAllForSelection());
        List<Map<String, String>> responseTypes = new ArrayList<>();
        for (IntentResponse.RESPONSE_TYPE responseType : IntentResponse.RESPONSE_TYPE.values()) {
            Map<String, String> dynamicResponseType = new HashMap<>();
            dynamicResponseType.put("code", responseType.name());
            dynamicResponseType.put("name", responseType.name());
            responseTypes.add(dynamicResponseType);
        }
        intent.getReferenceData().put("responseTypes", responseTypes);

        List<Map<String, String>> locales = new ArrayList<>();
        Collection<Language> supportedLangs = languageService.getAll();
        for (Language language : supportedLangs) {
            Map<String, String> localesMap = new HashMap<>();
            localesMap.put("code", language.getCode());
            localesMap.put("name", language.getName());
            locales.add(localesMap);
        }
        intent.getReferenceData().put("locales", locales);
    }

    public Intent initPredefinedIntent() {
        return createIntentModel(Intent.INTENT_TYPE.PREDEFINED);
    }

    private Intent createIntentModel(Intent.INTENT_TYPE predefined) {
        Intent mainIntent = new Intent();
        this.addReferenceData(mainIntent);
        mainIntent.setIntentType(predefined.name());
        // mainIntent.setOwner(accountService.getAuthenticatedUser());

        // set may be intent
        Intent mayBeIntent = new Intent();
        mainIntent.setMayBeIntent(mayBeIntent);
        IntentResponse mayResponse = new IntentResponse();
        mayBeIntent.getResponses().add(mayResponse);
        this.addReferenceData(mayBeIntent);

        return mainIntent;
    }

    public Intent initCustomIntent() {
        return createIntentModel(Intent.INTENT_TYPE.CUSTOM);
    }

    public Boolean uploadIntentsFromFile(@RequestPart("intentsData") MultipartFile file,
                                         @RequestPart("category") String categoryCode, Intent.INTENT_TYPE intentType) {
        if (!file.isEmpty() && StringUtils.isNotBlank(categoryCode)) {
            try {
                String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                String[] rows = fileContent.split("\n");
                Collection<Category> categories = this.categoryService.findFilteredCategoriesAllForSelection();
                Category category = categories.stream().filter(categoryOne
                        -> categoryCode.equals(categoryOne.getCode())).findAny().orElse(null);
                Map<String, Intent> intents = new HashMap<>();
                for (String row : rows) {
                    String[] cols = row.split(" ", 2);
                    LOGGER.debug("intent: {}, utterance: {}", cols[0], cols[1]);
                    String intentKey = cols[0].trim();
                    Intent currentIntent;
                    if (intents.keySet().contains(intentKey)) {
                        currentIntent = intents.get(intentKey);
                    } else {
                        currentIntent = new Intent();
                        currentIntent.setOwner(accountService.getAuthenticatedUser());
                        currentIntent.setIntentType(intentType.name());
                        currentIntent.setIntent(intentKey);
                        currentIntent.setCategory(category);
                        intents.put(intentKey, currentIntent);
                    }

                    if (currentIntent == null) {
                        throw new BaseRuntimeException(ErrorCodes.INTENTS_UPLOAD_ERROR);
                    }

                    IntentUtterance intentUtterance = new IntentUtterance();
                    intentUtterance.setLocale(Locale.ENGLISH.toString());
                    intentUtterance.setUtterance(cols[1].trim());
                    currentIntent.addIntentUtterance(intentUtterance);

                    if (currentIntent.getResponses().isEmpty()) {
                        IntentResponse intentResponse = new IntentResponse();
                        intentResponse.setLocale(Locale.ENGLISH.toString());
                        intentResponse.setResponse(cols[1].trim());
                        currentIntent.addIntentResponse(intentResponse);
                    }
                }
                LOGGER.debug("Final intent : {}", intents.size());
                this.saveAll(intents.values());
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public List<Intent> copyAllIntentsFromSourceToTarget(CopyIntents copyIntents) {

        /**
         * Check if the intents have already been copied over.
         */
        String targetCategoryCode = copyIntents.getTargetCategoryCode();
        String sourceCategoryCode = copyIntents.getSourceCategoryCode();
        Account currentUser = this.accountService.getAuthenticatedUser();

        List<Intent> copiedIntents = this.intentRepository.
                findIntentsByCategoryCodeAndOwnerAndCopyOfPredefinedIntentNotNull(targetCategoryCode,
                        accountService.getAuthenticatedUser());
        if (!copiedIntents.isEmpty()) {

            ValidationResult validationResult = new ValidationResult();
            ValidationException validationException = new ValidationException("DuplicateUtterances", validationResult);
            validationResult.addPageLevelError(this.helperService.getMessage(
                    "message.intents.predefined.intent.copied", new String[]{}));
            throw validationException;
        }

        /**
         * We will copy the intents, maybe intents and responses from the Generic category code and from the category
         * that the user selected.
         */
        Category targetCategoryDesiredByUser = this.categoryService.getCategoryByCode(targetCategoryCode);

        List<Intent> sourceIntents = new ArrayList<>();
        if (CommonConstants.PREDEFINED.equals(copyIntents.getSourceCategoryTypeCode())) {
            // get predefined Intents
            List<String> categoryCodes = Arrays.asList(sourceCategoryCode, this.helperService.getGenericCategoryCode());
            sourceIntents = this.findIntentsByCategoryAndType(categoryCodes, Intent.INTENT_TYPE.PREDEFINED.name());
        } else if (CommonConstants.CUSTOM.equals(copyIntents.getSourceCategoryTypeCode())) {
            // get custom Intents
            Category sourceCategory = this.categoryService.getCategoryByCode(sourceCategoryCode);
            if (!sourceCategory.getOwnerAccount().getUserName().equals(currentUser.getUserName())) {
                String message = "You are not authorized to copy intents.";
                List<String> messages = new ArrayList<>();
                messages.add(message);
                LOGGER.error(message);
                CommonUtils.showPageLevelValidationErrors("NotAuthorized", messages);
            }
            List<String> categoryCodes = Arrays.asList(sourceCategoryCode);
            sourceIntents = this.findIntentsByCategoryAndType(categoryCodes, Intent.INTENT_TYPE.CUSTOM.name());
        } else {
            CommonUtils.showPageLevelValidationErrors("InvalidCategoryCode",
                    Arrays.asList("Invalid Category Code Selected"));
        }

        List<Intent> copiedIntentsFresh = copySourceIntentsToTargetIntents(targetCategoryDesiredByUser, sourceIntents);
        this.saveAll(copiedIntentsFresh);
        return copiedIntentsFresh;
    }

    private List<Intent> copySourceIntentsToTargetIntents(Category categoryDesiredByUser, List<Intent> sourceIntents) {
        List<Intent> copiedIntents = new ArrayList<>();
        Account targetOwner = accountService.getAuthenticatedUser();
        for (Intent predefinedIntentUtterance : sourceIntents) {
            Intent customIntent = new Intent();
            customIntent.setOwner(targetOwner);
            customIntent.setCategory(categoryDesiredByUser);
            customIntent.setIntent(predefinedIntentUtterance.getIntent());
            customIntent.setIntentType(Intent.INTENT_TYPE.CUSTOM.name());
            customIntent.setCopyOfPredefinedIntent(predefinedIntentUtterance.getId());

            // copy the maybe intent
            Intent predefinedMayBeIntent = predefinedIntentUtterance.getMayBeIntent();
            if (predefinedMayBeIntent != null) {
                Intent mayBeIntent = new Intent();
                mayBeIntent.setCopyOfPredefinedIntent(predefinedMayBeIntent.getId());
                mayBeIntent.setIntentType(predefinedMayBeIntent.getIntentType());
                mayBeIntent.setCategory(categoryDesiredByUser);
                mayBeIntent.setOwner(targetOwner);
                mayBeIntent.setIntent(predefinedMayBeIntent.getIntent());

                // copy the predefined utterances
                for (IntentUtterance predefinedUtterance : predefinedMayBeIntent.getUtterances()) {
                    copyIntentUtterance(mayBeIntent, predefinedUtterance);
                }

                // copy the predefined responses
                for (IntentResponse predefinedResponse : predefinedMayBeIntent.getResponses()) {
                    copyIntentResponse(mayBeIntent, predefinedResponse);
                }

                // add the maybe intent to the custom intent
                customIntent.setMayBeIntent(mayBeIntent);
            }

            // copy the utterances
            for (IntentUtterance predefinedUtterance : predefinedIntentUtterance.getUtterances()) {
                copyIntentUtterance(customIntent, predefinedUtterance);
            }

            // copy the responses
            for (IntentResponse predefinedResponse : predefinedIntentUtterance.getResponses()) {
                copyIntentResponse(customIntent, predefinedResponse);
            }
            copiedIntents.add(customIntent);
        }
        return copiedIntents;
    }

    private void copyIntentResponse(Intent targetIntent, IntentResponse sourceResponse) {

        // avoid duplicates
        if (targetIntent.doesIntentAlreadyContainResponse(sourceResponse.getResponse())) {
            return;
        }

        IntentResponse customResponse = new IntentResponse();
        customResponse.setLocale(sourceResponse.getLocale());
        customResponse.setResponse(sourceResponse.getResponse());
        customResponse.setResponseType(sourceResponse.getResponseType());

        // add the response to the custom intent
        targetIntent.addIntentResponse(customResponse);
    }

    private void copyIntentUtterance(Intent targetCustomIntent, IntentUtterance sourceUtterance) {

        // avoid duplicates
        if (targetCustomIntent.doesIntentAlreadyContainUtterance(sourceUtterance.getUtterance())) {
            return;
        }

        IntentUtterance customUtterance = new IntentUtterance();
        customUtterance.setLocale(sourceUtterance.getLocale());
        customUtterance.setUtterance(sourceUtterance.getUtterance());

        // add the utterance to custom intent
        targetCustomIntent.addIntentUtterance(customUtterance);
    }

    /**
     * Export all of the intents into a CSV.
     *
     * @param categoryCode
     * @param intentType
     */
    public List<List<String>> exportIntents(String categoryCode, String intentType) {
        List<Intent> intents = this.intentRepository.
                findIntentsByCodeTypeAndOwner(categoryCode, intentType,
                        this.accountService.getAuthenticatedUser());

        List<List<String>> allIntents = new ArrayList<>();
        for (Intent intent : intents) {
            String intentName = intent.getIntent();
            Set<IntentUtterance> intentUtterances = intent.getUtterances();
            for (IntentUtterance intentUtterance : intentUtterances) {
                List<String> currentIntent = new ArrayList<>();
                currentIntent.add(intentName);
                currentIntent.add("^");
                currentIntent.add(intentUtterance.getUtterance());
                currentIntent.add("^");
                currentIntent.add(intentUtterance.getLocale() + System.lineSeparator());
                allIntents.add(currentIntent);
            }
        }
        return allIntents;
    }

    public List<IntentUtterance> findUtterances(SearchIntents searchIntents) {
        return this.intentRepository.findUtterances(searchIntents);
    }

    protected static Intent createStandardIntents(Category category, Account currentAccount, String intentName, String utterance) {
        // main intent
        Intent intent = new Intent();
        intent.setIntentType(Intent.INTENT_TYPE.CUSTOM.name());
        intent.setCategory(category);
        intent.setOwner(currentAccount);
        intent.setIntent(intentName);
        intent.addIntentResponse(createIntentResponse(intentName));

        // Maybe Intent
        Intent mayBeIntent = new Intent();
        mayBeIntent.setIntent("Maybe" + intentName);
        mayBeIntent.setOwner(currentAccount);
        mayBeIntent.setCategory(category);
        mayBeIntent.addIntentResponse(createIntentResponse(mayBeIntent.getIntent()));
        intent.setMayBeIntent(mayBeIntent);

        // utterances for the main intent
        IntentUtterance intentUtterance = new IntentUtterance();
        intentUtterance.setUtterance(utterance);
        intent.addIntentUtterance(intentUtterance);
        return intent;
    }

    protected static IntentResponse createIntentResponse(String response) {
        IntentResponse intentResponse = new IntentResponse();
        intentResponse.setResponseType(IntentResponse.RESPONSE_TYPE.STATIC.name());
        intentResponse.setResponse(response);
        return intentResponse;
    }

    /**
     * This method will allow you to copy intents from one category to the other. Here specific "intents" are copied
     *
     * @param copyIntents
     */
    public void copyIntentUtterancesFromSourceIntent(CopyIntents copyIntents) {
        Account currentUser = this.accountService.getAuthenticatedUser();

        Category sourceCategory = this.categoryService.getCategoryByCode(copyIntents.getSourceCategoryCode());

        SearchIntents searchSourceIntents = new SearchIntents();
        searchSourceIntents.setIntentNames(Arrays.asList(StringUtils.split(copyIntents.getIntentName(), ",")));
        searchSourceIntents.setCategory(sourceCategory);
        searchSourceIntents.setIgnoreOwnerAccount(true);
        List<Intent> sourceIntents = this.intentRepository.findIntentsAndUtterances(searchSourceIntents);
        LOGGER.debug("******* sourceIntents = " + sourceIntents.size());

        if (sourceIntents.size() > 1) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("Duplicate Source Intent found");
            CommonUtils.showPageLevelValidationErrors("MultipleSourceIntents", errorMessages);
        }

        if (sourceCategory.getType().equals(CommonConstants.CUSTOM) && !sourceCategory.getOwnerAccount()
                .getUserName().equals(currentUser.getUserName())) {
            String message = "You are not authorized to copy intents.";
            List<String> messages = new ArrayList<>();
            messages.add(message);
            LOGGER.error(message);
            CommonUtils.showPageLevelValidationErrors("NotAuthorized", messages);
        }

        // now get the target Intent
        SearchIntents searchTargetIntents = new SearchIntents();
        searchTargetIntents.setIntentNames(Arrays.asList(StringUtils.split(copyIntents.getTargetIntentName(), ",")));
        searchTargetIntents.setCategory(this.categoryService.getCategoryByCode(copyIntents.getTargetCategoryCode()));
        searchTargetIntents.setIgnoreOwnerAccount(true);
        List<Intent> targetIntents = this.intentRepository.findIntentsAndUtterances(searchTargetIntents);
        LOGGER.debug("******* targetIntents = " + targetIntents.size());

        if (targetIntents.size() > 1) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("Duplicate Target Intent found");
            CommonUtils.showPageLevelValidationErrors("MultipleTargetIntents", errorMessages);
        }

        // now copy the intent responses from source to target.
        Intent sourceIntent = sourceIntents.get(0);
        for (Intent targetIntent : targetIntents) {
            Set<IntentUtterance> sourceIntentUtterances = sourceIntent.getUtterances();
            int originalSize = targetIntent.getUtterances().size();
            for (IntentUtterance sourceUtterance : sourceIntentUtterances) {
                copyIntentUtterance(targetIntent, sourceUtterance);
            }
            int newSize = targetIntent.getUtterances().size();
            if (newSize > originalSize) {
                this.intentRepository.save(targetIntent);
            }
        }
    }
}
