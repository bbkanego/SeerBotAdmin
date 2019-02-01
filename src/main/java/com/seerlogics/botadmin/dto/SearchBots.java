package com.seerlogics.botadmin.dto;

import com.seerlogics.botadmin.model.Category;

/**
 * Created by bkane on 1/31/19.
 */
public class SearchBots extends BaseDto {
    private String name;
    private String displayName;
    private String description;
    private Category category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
