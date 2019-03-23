package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingoace.model.BaseModel;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bkane on 10/27/17.
 */
@Entity
@Table(name = "account", uniqueConstraints = @UniqueConstraint(columnNames = "user_name",
                        name = "acc_unique_user_name"))
public class Account extends BaseModel {

    // Since this is FK we DO NOT have CascadeType.PERSIST here. Since we do not want to persist the "detached" entity
    @ManyToOne(optional = false, fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "owner_id", nullable = false)
    private Party owner = new Party();

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Transient
    private String passwordCapture;

    @Transient
    private String passwordCaptureReenter;

    /**
     * http://www.java2s.com/Tutorials/Java/JPA/0820__JPA_OneToMany_Unidirectional.htm
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public String getPasswordCaptureReenter() {
        return passwordCaptureReenter;
    }

    public void setPasswordCaptureReenter(String passwordCaptureReenter) {
        this.passwordCaptureReenter = passwordCaptureReenter;
    }

    public String getPasswordCapture() {
        return passwordCapture;
    }

    public void setPasswordCapture(String passwordCapture) {
        this.passwordCapture = passwordCapture;
    }

    public Party getOwner() {
        return owner;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
