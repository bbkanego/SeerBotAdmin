package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchIntentsDto;
import com.seerlogics.botadmin.model.PredefinedIntentUtterances;

import java.util.List;

/**
 * Created by bkane on 1/28/19.
 */
public interface IntentSearchRepository {
    List<PredefinedIntentUtterances> findIntentsAndUtterances(SearchIntentsDto searchIntentsDto);
}
