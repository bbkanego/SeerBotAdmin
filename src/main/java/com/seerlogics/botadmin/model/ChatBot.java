package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 10/31/18.
 */
@Entity
@Table(name = "chat_bot")
@JsonTypeName("chat_bot")
public class ChatBot extends Bot {
    public ChatBot() {
        this.setType(BOT_TYPE.CHAT_BOT.name().toLowerCase());
    }
}
