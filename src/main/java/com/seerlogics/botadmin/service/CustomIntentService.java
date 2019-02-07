package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.CustomIntentUtterance;
import com.seerlogics.botadmin.repository.CustomPredefinedIntentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;

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
    private AccountService accountService;

    @Override
    public Collection<CustomIntentUtterance> getAll() {
        return customPredefinedIntentRepository.findAll();
    }

    @Override
    public CustomIntentUtterance getSingle(Long id) {
        CustomIntentUtterance customIntentUtterance = customPredefinedIntentRepository.getOne(id);
        customIntentUtterance.getReferenceData().put("categories", categoryService.getAll());
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
    public List<CustomIntentUtterance> saveAll(List<CustomIntentUtterance> predefinedIntentUtterances1) {
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
        customIntentUtterance.getReferenceData().put("categories", categoryService.getAll());
        customIntentUtterance.setOwner(accountService.getAuthenticatedUser());
        return customIntentUtterance;
    }
}
