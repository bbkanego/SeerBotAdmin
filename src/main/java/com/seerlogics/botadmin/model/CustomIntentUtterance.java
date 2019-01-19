package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "custom_intent_utterance",
        uniqueConstraints = @UniqueConstraint(columnNames = "utterance",
                name = "acc_utterance"))
public class CustomIntentUtterance extends BaseModel {
    @Column(nullable = false, length = 150)
    private String intent;

    @Column(nullable = false, length = 300)
    private String utterance;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
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
}
