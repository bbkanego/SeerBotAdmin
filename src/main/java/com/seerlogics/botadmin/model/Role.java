package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by bkane on 10/27/17.
 */
@Entity
@Table(name = "role",
        uniqueConstraints = {@UniqueConstraint(columnNames = "code", name = "role_unique_code"),
                @UniqueConstraint(columnNames = "role", name = "role_unique_role")})
public class Role extends BaseModel {

    @Column(nullable = false, length = 50)
    private String code;
    @Column(nullable = true, length = 1000)
    private String description;
    @Column(nullable = false, length = 50)
    private String role;
    @Column(nullable = false, length = 200)
    private String entitlements;

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
