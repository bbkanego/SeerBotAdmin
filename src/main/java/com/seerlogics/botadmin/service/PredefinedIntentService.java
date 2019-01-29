package com.seerlogics.botadmin.service;

import com.seerlogics.botadmin.dto.SearchIntentsDto;
import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.PredefinedIntentUtterances;
import com.seerlogics.botadmin.repository.PredefinedIntentRepository;
import com.lingoace.spring.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by bkane on 11/3/18.
 */
@Service
public class PredefinedIntentService extends BaseServiceImpl<PredefinedIntentUtterances> {

    @Autowired
    private PredefinedIntentRepository predefinedIntentRepository;

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

    public List<PredefinedIntentUtterances> findBy(SearchIntentsDto searchIntentsDto) {
        return predefinedIntentRepository.findIntentsAndUtterances(searchIntentsDto);
    }
}
