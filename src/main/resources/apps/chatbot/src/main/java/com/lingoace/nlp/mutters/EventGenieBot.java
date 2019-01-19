package com.lingoace.nlp.mutters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Created by bkane on 5/12/18.
 */
@Component
public class EventGenieBot {

    private EventGenieBotConfiguration eventGenieBotConfiguration;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public EventGenieBot(EventGenieBotConfiguration configuration) {
        this.eventGenieBotConfiguration = configuration;
    }

    public EventGenieBotConfiguration getEventGenieBotConfiguration() {
        return eventGenieBotConfiguration;
    }
}
