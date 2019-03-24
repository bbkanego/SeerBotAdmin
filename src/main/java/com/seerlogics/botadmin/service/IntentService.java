package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.exception.BaseRuntimeException;
import com.seerlogics.botadmin.exception.ErrorCodes;
import com.seerlogics.botadmin.model.*;
import com.seerlogics.botadmin.repository.IntentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional
public class IntentService extends BaseServiceImpl<Intent> {

    @Autowired
    private IntentRepository intentRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountService accountService;

    @Override
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
        // validate the intent. The intent should have only 1 response and MayBeResponse for a locale.
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
        searchIntents.getReferenceData().put("categories", categoryService.getAll());
        return searchIntents;
    }

    public List<Intent> findIntentsAndUtterances(SearchIntents searchIntents) {
        return intentRepository.findIntentsAndUtterances(searchIntents);
    }

    private void addReferenceData(Intent intent) {
        intent.getReferenceData().put("categories", categoryService.getAll());
        List<Map<String, String>> responseTypes = new ArrayList<>();
        for (IntentResponse.RESPONSE_TYPE responseType : IntentResponse.RESPONSE_TYPE.values()) {
            Map<String, String> dynamicResponseType = new HashMap<>();
            dynamicResponseType.put("code", responseType.name());
            dynamicResponseType.put("name", responseType.name());
            responseTypes.add(dynamicResponseType);
        }
        intent.getReferenceData().put("responseTypes", responseTypes);

        List<Map<String, String>> locales = new ArrayList<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            Map<String, String> localesMap = new HashMap<>();
            localesMap.put("code", locale.toString());
            localesMap.put("name", locale.getDisplayLanguage());
            locales.add(localesMap);
        }
        intent.getReferenceData().put("locales", locales);
    }

    public Intent initPredefinedIntent() {
        Intent intents = new Intent();
        this.addReferenceData(intents);
        intents.setIntentType(Intent.INTENT_TYPE.PREDEFINED.name());
        intents.setOwner(accountService.getAuthenticatedUser());
        return intents;
    }

    public Intent initCustomIntent() {
        Intent intents = new Intent();
        this.addReferenceData(intents);
        intents.setIntentType(Intent.INTENT_TYPE.CUSTOM.name());
        intents.setOwner(accountService.getAuthenticatedUser());
        return intents;
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
                    LOGGER.debug(String.format("intent: %s, utterance: %s", cols[0], cols[1]));
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

                    if (currentIntent.getResponses().size() == 0) {
                        IntentResponse intentResponse = new IntentResponse();
                        intentResponse.setLocale(Locale.ENGLISH.toString());
                        intentResponse.setResponse(cols[1].trim());
                        currentIntent.addIntentResponse(intentResponse);
                    }
                }
                LOGGER.debug("Final intent : " + intents.size());
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
}
