package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchBots;
import com.seerlogics.botadmin.model.Bot;

import java.util.List;

/**
 * Created by bkane on 1/31/19.
 */
public interface BotSearchRepository {
    List<Bot> findBots(SearchBots searchBots);
}
