package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Locale;

/**
 * Created by bkane on 11/11/18.
 */
@Entity
@Table(name = "language")
public class Language extends BaseModel {
    @Column(nullable = false, length = 50)
    private String code;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, length = 200)
    private String description;
    @Column(nullable = false, length = 5)
    private String locale;

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

    public Locale getLocale() {
        return new Locale(locale);
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
