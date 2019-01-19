package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by bkane on 11/20/18.
 */
@Entity
@Table(name = "status",
        uniqueConstraints = {@UniqueConstraint(columnNames = "code", name = "status_unique_code"),
                @UniqueConstraint(columnNames = "name", name = "status_unique_name")})
public class Status extends BaseModel {
    @Column(nullable = false, length = 50)
    private String code;
    @Column(nullable = true, length = 1000)
    private String description;
    @Column(nullable = false, length = 50)
    private String name;

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

    public enum STATUS_CODES {
        LAUNCHED, DRAFT, DEV, UAT, STOPPED
    }
}
