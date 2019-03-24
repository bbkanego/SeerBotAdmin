package com.seerlogics.chatbot.service;

import com.rabidgremlin.mutters.core.IntentMatch;
import com.seerlogics.chatbot.model.botadmin.Intent;
import com.seerlogics.chatbot.model.chat.ChatData;
import com.seerlogics.chatbot.mutters.EventGenieBot;
import com.seerlogics.chatbot.noggin.ChatSession;
import com.seerlogics.chatbot.repository.botadmin.IntentRepository;
import com.seerlogics.chatbot.repository.chat.ChatRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.*;

/**
 * Created by bkane on 4/15/18.
 */
@Service
public class ChatNLPService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private EventGenieBot eventGenieBot;

    @Autowired
    private VelocityEngine velocityEngine;

    /**
     * This needs to provided as a Java arg like "-Dseerchat.bottype=EVENT_BOT"
     */
    @Value("seerchat.bottype")
    private String botType;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IntentRepository intentRepository;

    public ChatData generateChatBotResponse(ChatData inputChatRequest, ChatSession chatSession) {
        // Save the incoming message
        String previousChatId = inputChatRequest.getPreviousChatId();
        if (StringUtils.isNotBlank(previousChatId)) {
            ChatData previousChat = chatRepository.getOne(Long.parseLong(previousChatId));
            inputChatRequest.setPreviousChat(previousChat);
            chatSession.setPreviousChat(previousChat);
        }
        ChatData savedInputChat = chatRepository.save(inputChatRequest);

        // create new reply message and save that to the DB
        ChatData outChatData = new ChatData();
        //outChatData.setResponse(intentProcessor.nlpPipeline(inputChatRequest, chatSession));
        IntentMatch match = eventGenieBot.getEventGenieBotConfiguration().
                getIntentMatcher().match(inputChatRequest.getMessage(), chatSession.getContext(), null, new HashMap<>());

        outChatData.setMessage(inputChatRequest.getMessage());

        if (chatSession.isConversationActive()) {
            String responseKey = chatSession.decideNextResponseInConversation(outChatData);
            Object customResponse = chatSession.getAttribute(responseKey + "CustomResponse");
            if (customResponse != null) {
                String finalResponse = (String)customResponse;
                if (finalResponse.contains("widget")) {
                    finalResponse = finalResponse.replaceAll("\\n", "");
                    outChatData.setResponse(finalResponse);
                } else {
                    outChatData.setResponse(convertToVelocityResponse(getMessage(finalResponse)));
                }
            } else {
                outChatData.setResponse(convertToVelocityResponse(getMessage(responseKey)));
            }
            chatSession.endCurrentConversationIfEndStateReached();
        } else if (match != null && chatSession.isIntentConversationStarter(match.getIntent().getName())) {
            chatSession.startConversation(match.getIntent().getName());
            String responseKey = chatSession.getCurrentStateMachineHandler().getCurrentState();
            outChatData.setResponse(convertToVelocityResponse(getMessage(responseKey)));
        } else {
            outChatData.setResponse(convertToVelocityResponse(getMessage(match)));
        }

        outChatData.setAccountId("ChatBot");
        outChatData.setChatSessionId(inputChatRequest.getChatSessionId());
        outChatData.setPreviousChatId(String.valueOf(savedInputChat.getId()));
        outChatData.setPreviousChat(savedInputChat);
        chatRepository.save(outChatData);

        return outChatData;
    }

    private String buildPlainTextResponse(String response) {
        VelocityContext context = new VelocityContext();
        context.put("response", response);
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate("/velocity/simpleTexts.vm", "UTF-8", context, stringWriter);
        return stringWriter.toString().replaceAll("\\n", "");
    }

    public ChatData generateInitiateChatResponse(ChatData inChat, ChatSession chatSession) {
        ChatData initiateResponse = new ChatData();
        initiateResponse.setAccountId("ChatBot");
        initiateResponse.setMessage(inChat.getMessage());
        initiateResponse.setResponse(loadInitiateResponse());
        String chatSessionId = UUID.randomUUID().toString();
        initiateResponse.setChatSessionId(chatSessionId);
        chatSession.setSessionId(chatSessionId);
        chatRepository.save(initiateResponse);
        return initiateResponse;
    }

    //todo fix logic here.
    private String getMessage(IntentMatch intent) {
        Intent dbIntent = intentRepository.findIntentsByUtterance(botType, Intent.INTENT_TYPE.CUSTOM.name(), intent.getUtterance(),
                "");
        dbIntent.getResponses();
        return "";
    }

    private String getMessage(String key) {
        return messageSource.getMessage("res_" + key.toLowerCase(), new Object[]{}, Locale.getDefault());
    }

    private String convertToVelocityResponse(String response) {
        if (response.contains("|")) {
            // this is an options response
            return buildOptionsResponse(response);
        } else { // its a plain response
            return buildPlainTextResponse(response);
        }
    }

    private String buildOptionsResponse(String response) {
        String[] messageParts = StringUtils.split(response, "|");
        String mainMessage = messageParts[0];
        List<Map> allOptions = new ArrayList<>(messageParts.length - 1);
        for (int i = 1, messagePartsLength = messageParts.length; i < messagePartsLength; i++) {
            Map<String, String> option = new HashMap<>();
            allOptions.add(option);
            String messagePart = messageParts[i];
            String[] optionParts = StringUtils.split(messagePart, "&");
            String firstPart = optionParts[0];
            String secondPart = optionParts[1];
            if (firstPart.contains("butt=")) {
                option.put("option", StringUtils.split(firstPart, "=")[1]);
                option.put("type", "button");
                option.put("clickResponse", StringUtils.split(secondPart, "=")[1]);
            } else if (firstPart.contains("link=")) {
                option.put("option", StringUtils.split(firstPart, "=")[1]);
                option.put("type", "link");
                option.put("clickResponse", StringUtils.split(secondPart, "=")[1]);
            }
        }
        VelocityContext context = new VelocityContext();
        context.put("allOptions", allOptions);
        context.put("message", mainMessage);
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate("/velocity/optionsResponse.vm", "UTF-8", context, stringWriter);
        return stringWriter.toString().replaceAll("\\n", "");
    }

    private String loadInitiateResponse() {
        String initResponse = messageSource.getMessage("res_initialResponse", new Object[]{}, Locale.getDefault());
        return buildOptionsResponse(initResponse);
    }

    public String getGenericConfirmMessage() {
        VelocityContext context = new VelocityContext();
        context.put("message", messageSource.getMessage("res_areYouSureYouWantToDelete",
                new Object[]{}, Locale.getDefault()));
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate("/velocity/deleteConfirm.vm", "UTF-8", context, stringWriter);
        return stringWriter.toString().replaceAll("\\n", "");
    }

    public String getSearchAllEventsOptionsMessage() {
        VelocityContext context = new VelocityContext();
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate("/velocity/searchEventsOptions.vm", "UTF-8", context, stringWriter);
        return stringWriter.toString().replaceAll("\\n", "");
    }

    public String getMayBeOptionsMessage(String intent) {
        VelocityContext context = new VelocityContext();
        context.put("message", messageSource.getMessage("res_" + intent.toLowerCase() + "_message",
                new Object[]{}, Locale.getDefault()));
        String options = messageSource.getMessage("res_" + intent.toLowerCase() + "_options", new Object[]{}, Locale.getDefault());
        String[] splitOptions = StringUtils.split(options, "*");
        List<Map> allOptions = new ArrayList<>(splitOptions.length);
        for (String splitOption : splitOptions) {
            Map<String, String> currentOption = new HashMap<>();
            String[] secSplitOptions = StringUtils.split(splitOption, "|");
            currentOption.put("whatCanBeDone", secSplitOptions[0]);
            currentOption.put("clickResponse", secSplitOptions[1]);
            allOptions.add(currentOption);
        }
        context.put("options", allOptions);
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate("/velocity/maybeOptions.vm", "UTF-8", context, stringWriter);
        return stringWriter.toString().replaceAll("\\n", "");
    }

    public List<ChatData> findByAccountId(String userName) {
        return chatRepository.findByAccountId(userName);
    }

    public List<ChatData> findAll() {
        return chatRepository.findAll();
    }

    public List<ChatData> findByChatSessionId(String chatsessionId) {
        return chatRepository.findByChatSessionId(chatsessionId);
    }

}
