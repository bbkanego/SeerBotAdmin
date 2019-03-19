package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lingoace.model.BaseModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bkane on 3/13/19.
 */
@Entity
@Table(name = "intent",
        uniqueConstraints = @UniqueConstraint(columnNames = "intent",
                name = "unq_intent"))
public class Intent extends BaseModel {

    public enum INTENT_TYPE {
        PREDEFINED, CUSTOM
    }

    @Column(nullable = false, length = 150)
    private String intent;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

    /*
    https://en.wikibooks.org/wiki/Java_Persistence/OneToMany
    The 'mappedBy = "owner"' attribute specifies that
    the 'private Intent owner;' field in IntentUtterance owns the
    relationship (i.e. contains the foreign key for the query to
    find all utterances for an intent.*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<IntentUtterance> utterances = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<IntentResponse> responses = new HashSet<>();

    @Column(nullable = false, length = 20)
    private String intentType = INTENT_TYPE.PREDEFINED.name();

    public String getIntentType() {
        return intentType;
    }

    public void setIntentType(String intentType) {
        this.intentType = intentType;
    }

    public Set<IntentResponse> getResponses() {
        return responses;
    }

    public void setResponses(Set<IntentResponse> responses) {
        this.responses = responses;
    }

    public Set<IntentUtterance> getUtterances() {
        return utterances;
    }

    public void setUtterances(Set<IntentUtterance> utterances) {
        this.utterances = utterances;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
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

    public void addIntentUtterance(IntentUtterance intentUtterance) {
        intentUtterance.setOwner(this);
        this.utterances.add(intentUtterance);
    }

    public void addIntentResponse(IntentResponse intentResponse) {
        intentResponse.setOwner(this);
        this.responses.add(intentResponse);
    }

    public List<IntentResponse> getIntentResponsesForLocale(String locale) {
        List<IntentResponse> intentResponses = new ArrayList<>();
        for (IntentResponse response : responses) {
            if (response.getLocale().equals(locale)) {
                intentResponses.add(response);
            }
        }
        return intentResponses;
    }
}
