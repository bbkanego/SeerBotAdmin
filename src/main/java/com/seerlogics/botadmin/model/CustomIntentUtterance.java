package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "custom_intent_utterance",
        uniqueConstraints = @UniqueConstraint(columnNames = "utterance",
                name = "ci_utterance"))
public class CustomIntentUtterance extends BaseModel {

    enum RESPONSE_TYPE {
        STATIC, DYNAMIC
    }

    @Column(nullable = false, length = 150)
    private String intent;

    @Column(nullable = false, length = 300)
    private String utterance;

    @Column(nullable = false, length = 8)
    private String locale = "en_us";

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

    @Column(nullable = false, length = 3000)
    private String response = "Please define response!";

    @Column(nullable = false, length = 20)
    private String responseType = RESPONSE_TYPE.STATIC.name();

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

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }
}
