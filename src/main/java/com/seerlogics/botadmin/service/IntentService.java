package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.exception.ErrorCodes;
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
public class IntentService extends BaseServiceImpl<Intent> {

    private final IntentRepository intentRepository;

    private final CategoryService categoryService;

    private final LanguageService languageService;

    private final AccountService accountService;

    public IntentService(IntentRepository intentRepository,
                         CategoryService categoryService, LanguageService languageService,
                         AccountService accountService) {
        this.intentRepository = intentRepository;
        this.categoryService = categoryService;
        this.languageService = languageService;
        this.accountService = accountService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN', 'ACCT_USER')")
    public Collection<Intent> getAll() {
        return intentRepository.findAll();
    }

    @Override
    public Intent getSingle(Long id) {
        Intent intent = intentRepository.getOne(id);
        addReferenceData(intent);
        return intent;
    }

    @Override
    public Intent save(Intent intent) {
        /*// validate the intent. The intent should have only 1 response and MayBeResponse for a locale.
        Set<IntentResponse> intentResponses = intent.getResponses();
        for (IntentResponse intentResponse : intentResponses) {
            List<IntentResponse> tempIntentResponses =
                    intent.getIntentResponsesForLocale(intentResponse.getLocale());
            if (tempIntentResponses.size() > 2) {
                throw new BaseRuntimeException(ErrorCodes.INTENTS_RESPONSE_NUM_ERROR);
            } else if (tempIntentResponses.size() == 2) {
                IntentResponse intentResponse1 = tempIntentResponses.get(0);
                IntentResponse intentResponse2 = tempIntentResponses.get(1);
                if (intentResponse1.getResponseType().equals(intentResponse2.getResponseType())) {
                    throw new BaseRuntimeException(ErrorCodes.DUPLICATE_INTENTS_RESPONSE_TYPE_ERROR);
                }
            }
        }*/
        List<IntentResponse> newResponses = new ArrayList<>(intent.getResponses());
        intent.getResponses().clear();
        intent.getResponses().addAll(newResponses);
        Intent mayBeIntent = intent.getMayBeIntent();
        if (mayBeIntent != null && mayBeIntent.getId() == null) {
            mayBeIntent.setOwner(intent.getOwner());
            mayBeIntent.setIntent("Maybe" + intent.getIntent());
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

    @Override
    public List<Intent> saveAll(Collection<Intent> predefinedIntentUtterances1) {
        return intentRepository.saveAll(predefinedIntentUtterances1);
    }

    public List<Intent> findByCategory(Category cat) {
        return intentRepository.findByCategory(cat);
    }

    public List<Intent> findIntentsByCategory(String catCode) {
        return intentRepository.findIntentsByCode(catCode);
    }

    public List<Intent> findIntentsByCategoryAndType(String catCode, String intentType) {
        return intentRepository.findIntentsByCodeAndType(catCode, intentType);
    }

    public List<Intent> findIntentsByCategoryTypeAndOwner(String catCode, String intentType) {
        return intentRepository.findIntentsByCodeTypeAndOwner(catCode, intentType, accountService.getAuthenticatedUser());
    }

    public SearchIntents initSearchIntentsCriteria(String type) {
        SearchIntents searchIntents = new SearchIntents();
        searchIntents.setIntentType(type);
        searchIntents.getReferenceData().put("categories", categoryService.finaAllForSelection());
        return searchIntents;
    }

    public List<Intent> findIntentsAndUtterances(SearchIntents searchIntents) {
        return intentRepository.findIntentsAndUtterances(searchIntents);
    }

    private void addReferenceData(Intent intent) {
        intent.getReferenceData().put("categories", categoryService.finaAllForSelection());
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
        mainIntent.setOwner(accountService.getAuthenticatedUser());

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
                Collection<Category> categories = this.categoryService.getAll();
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

    public List<Intent> copyPredefinedIntents(String categoryCode) {
        List<Intent> predefinedIntents = this.findIntentsByCategoryAndType(categoryCode,
                Intent.INTENT_TYPE.PREDEFINED.name());
        List<Intent> customIntents = new ArrayList<>();
        for (Intent predefinedIntentUtterance : predefinedIntents) {
            Intent customIntent = new Intent();
            customIntent.setOwner(accountService.getAuthenticatedUser());
            customIntent.setCategory(predefinedIntentUtterance.getCategory());
            customIntent.setIntent(predefinedIntentUtterance.getIntent());
            customIntent.setIntentType(Intent.INTENT_TYPE.CUSTOM.name());
            for (IntentUtterance predefinedUtterance : predefinedIntentUtterance.getUtterances()) {
                IntentUtterance customUtterance = new IntentUtterance();
                customIntent.addIntentUtterance(customUtterance);
                customUtterance.setLocale(predefinedUtterance.getLocale());
                customUtterance.setUtterance(predefinedUtterance.getUtterance());
            }
            for (IntentResponse predefinedResponse : predefinedIntentUtterance.getResponses()) {
                IntentResponse customResponse = new IntentResponse();
                customIntent.addIntentResponse(customResponse);
                customResponse.setLocale(predefinedResponse.getLocale());
                customResponse.setResponse(predefinedResponse.getResponse());
                customResponse.setResponseType(predefinedResponse.getResponseType());
            }
            customIntents.add(customIntent);
        }
        this.saveAll(customIntents);
        return customIntents;
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
}
