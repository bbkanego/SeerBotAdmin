package com.seerlogics.botadmin.dto;

import com.seerlogics.botadmin.model.Category;

/**
 * Created by bkane on 1/28/19.
 */
public class SearchIntents extends BaseDto {
    // define the criteria
    private String intentName;
    private String utterance;
    private Category category;

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
}