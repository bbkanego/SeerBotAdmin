package com.seerlogics.botadmin.dto;

import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.PredefinedIntentUtterances;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by bkane on 1/28/19.
 */
public class SearchIntentsDto extends BaseDto {
    // define the criteria
    private String intentName;
    private String utterance;
    private Category category;

    private Set<PredefinedIntentUtterances> searchResults = new HashSet<>();

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<PredefinedIntentUtterances> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(Set<PredefinedIntentUtterances> searchResults) {
        this.searchResults = searchResults;
    }
}