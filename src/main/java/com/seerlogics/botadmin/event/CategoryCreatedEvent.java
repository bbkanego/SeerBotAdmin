package com.seerlogics.botadmin.event;

import com.seerlogics.commons.model.Category;
import org.springframework.context.ApplicationEvent;

public class CategoryCreatedEvent extends ApplicationEvent {
    private Category category;

    public Category getCategory() {
        return category;
    }

    public CategoryCreatedEvent(Object source, Category category) {
        super(source);
        this.category = category;
    }
}
