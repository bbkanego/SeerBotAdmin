package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.dto.SearchIntents;
import com.seerlogics.commons.model.Category;
import com.seerlogics.commons.model.CustomIntentUtterance;
import com.seerlogics.commons.model.PredefinedIntentUtterances;
import com.seerlogics.commons.repository.CustomPredefinedIntentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by bkane on 11/3/18.
 */
@Service
@Transactional
public class CustomIntentService extends BaseServiceImpl<CustomIntentUtterance> {

    @Autowired
    private CustomPredefinedIntentRepository customPredefinedIntentRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PredefinedIntentService predefinedIntentService;

    @Autowired
    private AccountService accountService;

    @Override
    public Collection<CustomIntentUtterance> getAll() {
        return customPredefinedIntentRepository.findAll();
    }

    @Override
    public CustomIntentUtterance getSingle(Long id) {
        CustomIntentUtterance customIntentUtterance = customPredefinedIntentRepository.getOne(id);
        addReferenceData(customIntentUtterance);
        return customIntentUtterance;
    }

    @Override
    public CustomIntentUtterance save(CustomIntentUtterance object) {
        return customPredefinedIntentRepository.save(object);
    }

    @Override
    public void delete(Long id) {
        customPredefinedIntentRepository.deleteById(id);
    }

    @Override
    public List<CustomIntentUtterance> saveAll(Collection<CustomIntentUtterance> predefinedIntentUtterances1) {
        return customPredefinedIntentRepository.saveAll(predefinedIntentUtterances1);
    }

    public List<CustomIntentUtterance> findByCategory(Category cat) {
        return customPredefinedIntentRepository.findByCategory(cat);
    }

    public List<CustomIntentUtterance> findIntentsByCategory(String catCode) {
        return customPredefinedIntentRepository.findIntentsByCode(catCode);
    }

    public SearchIntents initSearchIntentsCriteria() {
        SearchIntents searchIntents = new SearchIntents();
        searchIntents.getReferenceData().put("categories", categoryService.getAll());
        return searchIntents;
    }

    public List<CustomIntentUtterance> findIntentsAndUtterances(SearchIntents searchIntents) {
        return customPredefinedIntentRepository.findIntentsAndUtterances(searchIntents);
    }

    public CustomIntentUtterance initCustomIntentUtterance() {
        CustomIntentUtterance customIntentUtterance = new CustomIntentUtterance();
        addReferenceData(customIntentUtterance);
        customIntentUtterance.setOwner(accountService.getAuthenticatedUser());
        return customIntentUtterance;
    }

    private void addReferenceData(CustomIntentUtterance customIntentUtterance) {
        customIntentUtterance.getReferenceData().put("categories", categoryService.getAll());
        List<Map<String, String>> responseTypes = new ArrayList<>();
        Map<String, String> dynamicResponseType = new HashMap<>();
        dynamicResponseType.put("code", PredefinedIntentUtterances.RESPONSE_TYPE.DYNAMIC.name());
        dynamicResponseType.put("name", "Dynamic");
        Map<String, String> staticResponseType = new HashMap<>();
        staticResponseType.put("code", PredefinedIntentUtterances.RESPONSE_TYPE.STATIC.name());
        staticResponseType.put("name", "Static");
        responseTypes.add(dynamicResponseType);
        responseTypes.add(staticResponseType);
        customIntentUtterance.getReferenceData().put("responseTypes", responseTypes);
    }

    public List<CustomIntentUtterance> copyStandardIntents(String categoryCode) {
        List<PredefinedIntentUtterances> predefinedIntentUtterances =
                this.predefinedIntentService.findIntentsByCategory(categoryCode);
        List<CustomIntentUtterance> customIntentUtterances = new ArrayList<>();
        for (PredefinedIntentUtterances predefinedIntentUtterance : predefinedIntentUtterances) {
            CustomIntentUtterance customIntentUtterance = new CustomIntentUtterance();
            customIntentUtterance.setOwner(accountService.getAuthenticatedUser());
            customIntentUtterance.setCategory(predefinedIntentUtterance.getCategory());
            customIntentUtterance.setIntent(predefinedIntentUtterance.getIntent());
            customIntentUtterance.setUtterance(predefinedIntentUtterance.getUtterance());
            customIntentUtterance.setLocale(predefinedIntentUtterance.getLocale());
            customIntentUtterance.setResponse(predefinedIntentUtterance.getResponse());
            customIntentUtterance.setResponseType(predefinedIntentUtterance.getResponseType());
            customIntentUtterances.add(customIntentUtterance);
        }
        this.saveAll(customIntentUtterances);
        return customIntentUtterances;
    }

    public boolean saveIntentsFromFile(byte[] fileBytes, String categoryCode) {
        try {
            String fileContent = new String(fileBytes, StandardCharsets.UTF_8);
            String[] rows = fileContent.split("\n");
            List<CustomIntentUtterance> customIntentUtteranceList = new ArrayList<>();
            Collection<Category> categories = this.categoryService.getAll();
            Category category = categories.stream().filter(categoryOne -> categoryCode.equals(categoryOne.getCode())).findAny().orElse(null);
            for (String row : rows) {
                String[] cols = row.split(" ", 2);
                //LOGGER.debug(String.format("intent: %s, utterance: %s", cols[0], cols[1]));
                CustomIntentUtterance customIntentUtterance = new CustomIntentUtterance();
                customIntentUtterance.setCategory(category);
                customIntentUtterance.setIntent(cols[0].trim());
                customIntentUtterance.setUtterance(cols[1].trim());
                customIntentUtterance.setOwner(this.accountService.getAuthenticatedUser());
                customIntentUtteranceList.add(customIntentUtterance);
            }
            this.saveAll(customIntentUtteranceList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
