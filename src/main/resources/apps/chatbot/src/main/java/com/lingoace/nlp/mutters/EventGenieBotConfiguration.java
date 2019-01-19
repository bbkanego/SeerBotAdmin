package com.lingoace.nlp.mutters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingoace.common.NLPProcessingException;
import com.lingoace.nlp.service.ChatDataFetchService;
import com.lingoace.nlp.service.ChatNLPService;
import com.rabidgremlin.mutters.bot.ink.StoryUtils;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPTokenizer;
import com.rabidgremlin.mutters.opennlp.ner.OpenNLPSlotMatcher;
import com.rabidgremlin.mutters.slots.LiteralSlot;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bkane on 5/10/18.
 */
@Component
public class EventGenieBotConfiguration {

    private static final Logger LOGGER = Logger.getLogger(EventGenieBotConfiguration.class);

    @Autowired
    private ChatDataFetchService chatDataFetchService;

    @Autowired
    private ChatNLPService chatNLPService;

    private IntentMatcher intentMatcher;

    private SentenceDetectorME sentenceDetectorME;

    private String chatBotStory;

    @Autowired
    private MessageSource messageSource;

    private List<GlobalIntent> globalIntents = new ArrayList<>();

    @PostConstruct
    private void init() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        URL botConfigURL = Thread.currentThread().getContextClassLoader().getResource("bot/config/botConfig.json");
        BotConfiguration botConfiguration = mapper.readValue(botConfigURL, BotConfiguration.class);
        LOGGER.debug("The configuration is = " + mapper.writeValueAsString(botConfiguration));

        //Loading sentence detector model
        String sentModel = botConfiguration.getSentenceDetectModel();
        if (sentModel != null) {
            try {
                // https://www.tutorialspoint.com/opennlp/opennlp_sentence_detection.htm
                URL enSentenceDetectModelUrl = Thread.currentThread().getContextClassLoader().
                        getResource(sentModel);
                if (enSentenceDetectModelUrl != null) {
                    SentenceModel sentenceDetectModel = new SentenceModel(enSentenceDetectModelUrl);
                    //Instantiating the SentenceDetectorME class
                    sentenceDetectorME = new SentenceDetectorME(sentenceDetectModel);
                } else {
                    throw new NLPProcessingException("'nlp/models/standard/en-sent.bin' not found!");
                }
            } catch (IOException e) {
                throw new NLPProcessingException(e);
            }
        }

        OpenNLPTokenizer openNLPTokenizer = null;
        String tokenizerModel = botConfiguration.getTokenizerModel();
        if (tokenizerModel != null) {

            // model was built with OpenNLP whitespace tokenizer
            URL enTokenModelUrl = Thread.currentThread().getContextClassLoader().getResource(tokenizerModel);
            TokenizerModel model = null;
            try {
                if (enTokenModelUrl != null) {
                    model = new TokenizerModel(enTokenModelUrl);
                } else {
                    throw new NLPProcessingException("'" + tokenizerModel + "' not found!");
                }
            } catch (IOException e) {
                throw new NLPProcessingException(e);
            }
            Tokenizer tokenBasedTokenizer = new TokenizerME(model);
            openNLPTokenizer = new CustomOpenNLPTokenizer(tokenBasedTokenizer);
        } else {
            throw new NLPProcessingException("Config Error: No Tokenizer model defined");
        }

        // use OpenNLP NER for slot matching
        List<SlotMatcherModel> slotMatcherModels = botConfiguration.getSlotMatcherModels();
        OpenNLPSlotMatcher slotMatcher = new OpenNLPSlotMatcher(openNLPTokenizer);
        for (SlotMatcherModel slotMatcherModel : slotMatcherModels) {
            slotMatcher.addSlotModel(slotMatcherModel.getType(), slotMatcherModel.getModel());
        }

        /**
         * create intent matcher
         * I have created the matcher with min score of 0.20f so that we can get some kind of match with intents when
         * the conversation is close to what we think it is.
         */
        CustomOpenNLPIntentMatcher matcher =
                new CustomOpenNLPIntentMatcher(botConfiguration.getNlpIntentMatcher().getIntentModel(), openNLPTokenizer,
                        slotMatcher, Float.parseFloat(botConfiguration.getNlpIntentMatcher().getMinMatchScore()),
                        Float.parseFloat(botConfiguration.getNlpIntentMatcher().getMaybeMatchScore()));

        List<com.lingoace.nlp.mutters.Intent> intents = botConfiguration.getIntents();
        for (com.lingoace.nlp.mutters.Intent intent : intents) {
            Intent currentIntent = new Intent(intent.getIntent());
            matcher.addIntent(currentIntent);
            List<String> slots = intent.getSlots();
            for (String slot : slots) {
                currentIntent.addSlot(new LiteralSlot(slot));
            }
        }

        this.intentMatcher = matcher;

        this.chatBotStory = StoryUtils.loadStoryJsonFromClassPath(botConfiguration.getInkStory());


    }

    public SentenceDetectorME getSentenceDetector() {
        return sentenceDetectorME;
    }

    public IntentMatcher getIntentMatcher() {
        return intentMatcher;
    }

    public String getStoryJson() {
        return chatBotStory;
    }

    public List<GlobalIntent> getGlobalIntents() {
        return globalIntents;
    }
}
