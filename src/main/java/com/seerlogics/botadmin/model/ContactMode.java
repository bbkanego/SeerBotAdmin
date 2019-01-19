package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lingoace.model.BaseModel;

import javax.persistence.*;

/**
 * Created by bkane on 11/3/18.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "contact_mode")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = Address.class, name = "street_address")})
public class ContactMode extends BaseModel {
    @Column(name = "type", nullable = false)
    private String type;

    public String getType() {
        return type.toLowerCase();
    }

    public void setType(String type) {
        this.type = type;
    }

    public enum CONTACT_TYPE {
        STREET_ADDRESS, PHONE, EMAIL, SKYPE, FACEBOOK, WHATSAPP
    }
}
