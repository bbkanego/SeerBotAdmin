package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lingoace.model.BaseModel;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bkane on 5/31/17.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "party")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = Person.class, name = "person"),
        @JsonSubTypes.Type(value = Organization.class, name = "organization")})
public class Party extends BaseModel {

    @Column(nullable = false)
    @JsonIgnore
    private String type;
    @Column(nullable = false, length = 200)
    private String name;
    /**
     * http://www.java2s.com/Tutorials/Java/JPA/0820__JPA_OneToMany_Unidirectional.htm
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "party_contact_mode",
            joinColumns = @JoinColumn(name = "party_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_mode_id"))
    private Set<ContactMode> contactModes = new HashSet<>();

    public Set<ContactMode> getContactModes() {
        return contactModes;
    }

    public void setContactModes(Set<ContactMode> contactModes) {
        this.contactModes = contactModes;
    }

    public void addContactMode(ContactMode contactMode) {
        this.contactModes.add(contactMode);
    }

    public String getType() {
        return type.toLowerCase();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum PART_TYPE {
        PERSON, ORGANIZATION
    }
}
