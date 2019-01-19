package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 11/11/18.
 */
@Entity
@Table(name = "voice_bot")
@JsonTypeName("voice_bot")
public class VoiceBot extends Bot {
    public VoiceBot() {
        this.setType(BOT_TYPE.VOICE_BOT.name().toLowerCase());
    }
}
