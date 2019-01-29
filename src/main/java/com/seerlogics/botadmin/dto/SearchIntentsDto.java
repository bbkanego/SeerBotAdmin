package com.seerlogics.botadmin.dto;

import com.seerlogics.botadmin.model.Category;

/**
 * Created by bkane on 1/28/19.
 */
public class SearchIntentsDto extends BaseDto {
    // define the criteria
    private String intentName;
    private String utterance;
    private Category category;

    //private Set<PredefinedIntentUtterances>
}