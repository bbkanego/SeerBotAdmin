package com.lingoace.nlp.mutters;

import java.util.ArrayList;
import java.util.List;

public class Intent {

    private String intent;
    private List<String> slots = new ArrayList<>();

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<String> getSlots() {
        return slots;
    }

    public void setSlots(List<String> slots) {
        this.slots = slots;
    }

}
