package com.seerlogics.chatbot.model.botadmin;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lingoace.model.BaseModel;

import javax.persistence.*;
import java.util.Locale;

/**
 * Created by bkane on 3/13/19.
 */
@Entity
@Table(name = "intent_utterance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utterance", "owner_intent_id", "locale"},
                name = "unq_utterance_per_locale"))
public class IntentUtterance extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_intent_id", nullable = false)
    @JsonBackReference
    private Intent owner;

    @Column(nullable = false, length = 300)
    private String utterance;

    @Column(name = "locale", nullable = false, length = 8)
    private String locale = Locale.ENGLISH.toString();

    public Intent getOwner() {
        return owner;
    }

    public void setOwner(Intent owner) {
        this.owner = owner;
    }

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
