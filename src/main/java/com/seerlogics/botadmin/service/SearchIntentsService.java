package com.seerlogics.botadmin.service;

import com.seerlogics.botadmin.dto.SearchIntentsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bkane on 1/28/19.
 */
@Service
public class SearchIntentsService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PredefinedIntentService predefinedIntentService;

    public SearchIntentsDto initCriteria() {
        SearchIntentsDto searchIntentsDto = new SearchIntentsDto();
        searchIntentsDto.getReferenceData().put("categories", categoryService.getAll());
        return searchIntentsDto;
    }

    public SearchIntentsDto search(SearchIntentsDto searchIntentsDto) {
        return searchIntentsDto;
    }


}
