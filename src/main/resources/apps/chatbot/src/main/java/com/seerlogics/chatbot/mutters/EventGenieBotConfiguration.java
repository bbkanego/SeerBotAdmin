package com.seerlogics.chatbot.mutters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingoace.common.NLPProcessingException;
import com.rabidgremlin.mutters.bot.ink.StoryUtils;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPTokenizer;
import com.rabidgremlin.mutters.opennlp.ner.OpenNLPSlotMatcher;
import com.seerlogics.chatbot.repository.botadmin.IntentRepository;
import com.seerlogics.chatbot.service.ChatDataFetchService;
import com.seerlogics.chatbot.service.ChatNLPService;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private IntentRepository intentRepository;

    @Autowired
    private ChatNLPService chatNLPService;

    /**
     * This needs to provided as a Java arg like "-Dseerchat.bottype=EVENT_BOT"
     */
    @Value("${seerchat.bottype}")
    private String botType;

    /**
     * This needs to provided as a Java arg like "-Dseerchat.botOwnerId=354243"
     */
    @Value("${seerchat.botOwnerId}")
    private String botOwnerId;

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

        LOGGER.debug("\n*********Set up tokenizer\n");

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

        LOGGER.debug("\n*********Set getSlotMatcherModels\n");

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

        LOGGER.debug("\n*********get customIntentUtterances\n");

        // List<com.seerlogics.chatbot.mutters.Intent> intents = botConfiguration.getIntents();
        List<com.seerlogics.chatbot.model.botadmin.Intent> customIntentUtterances =
                            intentRepository.findIntentsByCodeAndType(this.botType,
                                    com.seerlogics.chatbot.model.botadmin.Intent.INTENT_TYPE.CUSTOM.name(),
                                    Long.parseLong(this.botOwnerId));
        for (com.seerlogics.chatbot.model.botadmin.Intent customIntentUtterance : customIntentUtterances) {
            Intent currentIntent = new Intent(customIntentUtterance.getIntent(), customIntentUtterance);
            matcher.addIntent(currentIntent);
        }

        LOGGER.debug("\n*********Done******\n");

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
