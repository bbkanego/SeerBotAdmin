package com.seerlogics.chatbot.model.botadmin;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lingoace.model.BaseModel;

import javax.persistence.*;
import java.util.Locale;

/**
 * Created by bkane on 3/13/19.
 */
@Entity
@Table(name = "intent_response",
        uniqueConstraints = @UniqueConstraint(columnNames = {"response", "owner_intent_id", "locale"},
                name = "unq_response"))
public class IntentResponse extends BaseModel {

    public enum RESPONSE_TYPE {
        STATIC, DYNAMIC, MAYBE
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_intent_id", nullable = false)
    @JsonBackReference
    private Intent owner;

    @Column(nullable = false, length = 3000)
    private String response = "Please define response!";

    @Column(nullable = false, length = 20)
    private String responseType = RESPONSE_TYPE.STATIC.name();

    @Column(name = "locale", nullable = false, length = 8)
    private String locale = Locale.ENGLISH.toString();

    public Intent getOwner() {
        return owner;
    }

    public void setOwner(Intent owner) {
        this.owner = owner;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
