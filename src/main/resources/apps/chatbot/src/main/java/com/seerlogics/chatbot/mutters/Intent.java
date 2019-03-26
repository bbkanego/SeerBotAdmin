package com.seerlogics.chatbot.mutters;

import java.util.ArrayList;
import java.util.List;

public class Intent extends com.rabidgremlin.mutters.core.Intent {

    private com.seerlogics.chatbot.model.botadmin.Intent dbIntent;

    public Intent(String name, com.seerlogics.chatbot.model.botadmin.Intent dbIntent) {
        super(name);
        this.dbIntent = dbIntent;
    }

    public com.seerlogics.chatbot.model.botadmin.Intent getDbIntent() {
        return dbIntent;
    }
}
