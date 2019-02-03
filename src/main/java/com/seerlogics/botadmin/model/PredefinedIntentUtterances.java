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
                name = "acc_utterance"))
public class PredefinedIntentUtterances extends BaseModel {
    @Column(nullable = false, length = 150)
    private String intent;

    @Column(nullable = false, length = 300)
    private String utterance;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

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
}
