package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.*;

/**
 * Created by bkane on 10/27/17.
 */
@Entity
@Table(name = "role",
        uniqueConstraints = {@UniqueConstraint(columnNames = "code", name = "role_unique_code"),
                @UniqueConstraint(columnNames = "role", name = "role_unique_role")})
public class Role extends BaseModel {

    public enum ROLE_TYPE {
        /**
         * Admin for a company account
         */
        ACCT_ADMIN,
        /**
         * USER defined by admin to manage account
         */
        ACCT_USER,
        /**
         * USER defined by admin to view account
         */
        ACCT_VIEW,
        /**
         * Uber Adiminstrator who will have "root" access to the Bot Admin application
         * entitilents: READ,WRITE,UPDATE,DELETE,UBER_ADMIN
         */
        UBER_ADMIN
    }

    @Transient
    private String name;
    @Column(nullable = false, length = 50)
    private String code;
    @Column(nullable = true, length = 1000)
    private String description;
    @Column(nullable = false, length = 50)
    private String role;
    @Column(nullable = false, length = 200)
    private String entitlements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(String entitlements) {
        this.entitlements = entitlements;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum ENTITLEMENT {
        READ, WRITE, DELETE, UPDATE
    }
}
