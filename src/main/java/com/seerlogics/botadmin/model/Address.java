package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 5/31/17.
 */
@Entity
@Table(name = "address")
@JsonTypeName("address")
public class Address extends ContactMode {
    @Column(nullable = false, length = 200)
    private String street;
    @Column(nullable = false, length = 100)
    private String city;
    @Column(nullable = false, length = 100)
    private String state;
    @Column(nullable = false, length = 100)
    private String zip;

    public Address() {
        this.setType(CONTACT_TYPE.STREET_ADDRESS.name());
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
