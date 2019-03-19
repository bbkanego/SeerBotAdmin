package com.seerlogics.botadmin.service;

import com.seerlogics.botadmin.model.Language;
import com.seerlogics.botadmin.repository.LanguageRepository;
import com.lingoace.spring.service.BaseServiceImpl;
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
public class LanguageService extends BaseServiceImpl<Language> {

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    public Collection<Language> getAll() {
        return languageRepository.findAll();
    }

    @Override
    public Language getSingle(Long id) {
        return languageRepository.getOne(id);
    }

    @Override
    public Language save(Language object) {
        return languageRepository.save(object);
    }

    @Override
    public void delete(Long id) {
        languageRepository.deleteById(id);
    }

    @Override
    public List<Language> saveAll(Collection<Language> languages) {
        return languageRepository.saveAll(languages);
    }
}
