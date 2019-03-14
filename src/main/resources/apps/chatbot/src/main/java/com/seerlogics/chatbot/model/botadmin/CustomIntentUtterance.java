package com.seerlogics.chatbot.model.botadmin;

import com.lingoace.model.BaseModel;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "custom_intent_utterance")
@Immutable
public class CustomIntentUtterance extends BaseModel {

    enum RESPONSE_TYPE {
        STATIC, DYNAMIC
    }

    @Column(nullable = false, length = 150, updatable = false)
    private String intent;

    @Column(nullable = false, length = 300, updatable = false)
    private String utterance;

    @Column(nullable = false, length = 8, updatable = false)
    private String locale = "en_us";

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", updatable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_id", nullable = false, updatable = false)
    private Account owner;

    @Column(nullable = false, length = 3000, updatable = false)
    private String response = "Please define response!";

    @Column(name = "response_type", nullable = false, length = 20, updatable = false)
    private String responseType = RESPONSE_TYPE.STATIC.name();

    public String getResponse() {
        return response;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getLocale() {
        return locale;
    }

    public String getIntent() {
        return intent;
    }

    public String getUtterance() {
        return utterance;
    }

    public Category getCategory() {
        return category;
    }

    public Account getOwner() {
        return owner;
    }
}
