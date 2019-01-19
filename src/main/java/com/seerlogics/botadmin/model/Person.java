package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 5/30/17.
 */
@Entity
@Table(name = "person")
@JsonTypeName("person")
public class Person extends Party {

    @Column(name = "first_name", nullable = false, length = 200)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 200)
    private String lastName;

    public Person() {
        this.setType(PART_TYPE.PERSON.name());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        setName(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
