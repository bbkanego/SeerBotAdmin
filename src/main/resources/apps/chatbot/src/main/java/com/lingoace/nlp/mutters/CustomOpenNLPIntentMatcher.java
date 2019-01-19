package com.lingoace.nlp.mutters;

import com.rabidgremlin.mutters.core.SlotMatcher;
import com.rabidgremlin.mutters.core.Tokenizer;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPIntentMatcher;

import java.net.URL;

/**
 * Created by bkane on 9/2/18.
 */
public class CustomOpenNLPIntentMatcher extends OpenNLPIntentMatcher {
    public CustomOpenNLPIntentMatcher(String intentModel, Tokenizer tokenizer, SlotMatcher slotMatcher) {
        super(intentModel, tokenizer, slotMatcher);
    }

    public CustomOpenNLPIntentMatcher(String intentModel, Tokenizer tokenizer, SlotMatcher slotMatcher, float minMatchScore, float maybeMatchScore) {
        super(intentModel, tokenizer, slotMatcher, minMatchScore, maybeMatchScore);
    }

    public CustomOpenNLPIntentMatcher(URL intentModelUrl, Tokenizer tokenizer, SlotMatcher slotMatcher, float minMatchScore, float maybeMatchScore) {
        super(intentModelUrl, tokenizer, slotMatcher, minMatchScore, maybeMatchScore);
    }
}
