package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 5/30/17.
 */
@Entity
@Table(name = "organization")
@JsonTypeName("organization")
public class Organization extends Party {

    @Column(nullable = false, length = 200)
    private String name;
    @Column(nullable = false, length = 200)
    private String dba; // doing business as

    public Organization() {
        this.setType(PART_TYPE.ORGANIZATION.name());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getDba() {
        return dba;
    }

    public void setDba(String dba) {
        this.dba = dba;
    }
}
