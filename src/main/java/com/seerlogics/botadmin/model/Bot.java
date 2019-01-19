package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lingoace.model.BaseModel;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bkane on 11/11/18.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "bot", uniqueConstraints = {@UniqueConstraint(columnNames = "name", name = "bot_unique_name")})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = Bot.class)
@JsonSubTypes({@JsonSubTypes.Type(value = VoiceBot.class, name = "voice_bot"),
                @JsonSubTypes.Type(value = ChatBot.class, name = "chat_bot")})
public class Bot extends BaseModel {
    @Column(nullable = false)
    private String type;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 100)
    private String displayName;
    @Column(length = 3000)
    private String description;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner = new Account();
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;
    /**
     * http://www.java2s.com/Tutorials/Java/JPA/0820__JPA_OneToMany_Unidirectional.htm
     * https://en.wikibooks.org/wiki/Java_Persistence/ManyToMany
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "bot_language",
            joinColumns = @JoinColumn(name = "bot_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "language_id", referencedColumnName = "ID"))
    private Set<Language> supportedLanguages = new HashSet<>();
    /**
     * https://en.wikibooks.org/wiki/Java_Persistence/OneToMany
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "bot_configuration",
            joinColumns = {@JoinColumn(name = "bot_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "config_id", referencedColumnName = "id", unique = true)})
    private Set<Configuration> configurations = new HashSet<>();

    public Set<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<Language> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(Set<Language> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public enum BOT_TYPE {
        CHAT_BOT, VOICE_BOT
    }
}
