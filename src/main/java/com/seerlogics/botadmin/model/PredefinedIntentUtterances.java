package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.*;

/**
 * Created by bkane on 11/15/18.
 * This table will contain ALL the predefined intent and utterances for specific types
 * of bots defined by the category.
 * There will be a separate table for client defined intents and utterances.
 * The customer will not be allowed to edi this. They can copy these over to custom intents and edit those.
 */
@Entity
@Table(name = "predefined_intent_utterance",
        uniqueConstraints = @UniqueConstraint(columnNames = "utterance",
                name = "pdi_utterance"))
public class PredefinedIntentUtterances extends BaseModel {

    public enum RESPONSE_TYPE {
        STATIC, DYNAMIC
    }

    @Column(nullable = false, length = 150)
    private String intent;

    @Column(nullable = false, length = 300)
    private String utterance;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

    @Column(nullable = false, length = 8)
    private String locale = "en_us";

    @Column(nullable = false, length = 3000)
    private String response = "Please define response!";

    @Column(nullable = false, length = 20)
    private String responseType = RESPONSE_TYPE.STATIC.name();

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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
