package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.CustomIntentUtterance;

import java.util.List;

/**
 * Created by bkane on 1/28/19.
 */
public interface CustomIntentSearchRepository {
    List<CustomIntentUtterance> findIntentsAndUtterances(SearchIntents searchIntents);
}

