package com.seerlogics.chatbot.model.botadmin;

import com.lingoace.model.BaseModel;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 10/31/18.
 */
@Entity
@Table(name = "category")
@Immutable
public class Category extends BaseModel {
    @Column(length = 50, nullable = false, updatable = false)
    private String code;

    @Column(nullable = false, updatable = false)
    private String name;

    @Column(length = 3000, updatable = false)
    private String description;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
