package com.seerlogics.chatbot.model.botadmin;

import com.lingoace.model.BaseModel;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 10/27/17.
 */
@Entity
@Table(name = "account")
@Immutable
public class Account extends BaseModel {

    @Column(name = "user_name", nullable = false, updatable = false)
    private String userName;

    public String getUserName() {
        return userName;
    }
}
