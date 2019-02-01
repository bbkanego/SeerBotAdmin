package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.PredefinedIntentUtterances;
import com.seerlogics.botadmin.repository.PredefinedIntentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 11/3/18.
 */
@Service
public class PredefinedIntentService extends BaseServiceImpl<PredefinedIntentUtterances> {

    @Autowired
    private PredefinedIntentRepository predefinedIntentRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Collection<PredefinedIntentUtterances> getAll() {
        return predefinedIntentRepository.findAll();
    }

    @Override
    public PredefinedIntentUtterances getSingle(Long id) {
        return predefinedIntentRepository.getOne(id);
    }

    @Override
    public PredefinedIntentUtterances save(PredefinedIntentUtterances object) {
        return predefinedIntentRepository.save(object);
    }

    @Override
    public void delete(Long id) {
        predefinedIntentRepository.deleteById(id);
    }

    @Override
    public List<PredefinedIntentUtterances> saveAll(List<PredefinedIntentUtterances> predefinedIntentUtterances1) {
        return predefinedIntentRepository.saveAll(predefinedIntentUtterances1);
    }

    public List<PredefinedIntentUtterances> findByCategory(Category cat) {
        return predefinedIntentRepository.findByCategory(cat);
    }

    public List<PredefinedIntentUtterances> findIntentsByCategory(String catCode) {
        return predefinedIntentRepository.findIntentsByCode(catCode);
    }

    public SearchIntents initSearchIntentsCriteria() {
        SearchIntents searchIntents = new SearchIntents();
        searchIntents.getReferenceData().put("categories", categoryService.getAll());
        return searchIntents;
    }

    public List<PredefinedIntentUtterances> findIntentsAndUtterances(SearchIntents searchIntents) {
        return predefinedIntentRepository.findIntentsAndUtterances(searchIntents);
    }
}
