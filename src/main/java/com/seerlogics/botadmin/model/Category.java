package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by bkane on 10/31/18.
 */
@Entity
@Table(name = "category",
        uniqueConstraints = {@UniqueConstraint(columnNames = "name", name = "cat_unique_name"),
                @UniqueConstraint(columnNames = "code", name = "cat_unique_code")})
public class Category extends BaseModel {
    @Column(length = 50, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 3000)
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
