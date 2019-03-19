package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Intent;

import java.util.List;

/**
 * Created by bkane on 3/14/19.
 */
public interface IntentSearchRepository {
    List<Intent> findIntentsAndUtterances(SearchIntents searchIntents);
}
