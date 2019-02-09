package com.seerlogics.chatbot.controller;

import com.seerlogics.chatbot.model.ChatData;
import com.seerlogics.chatbot.noggin.ChatSession;
import com.seerlogics.chatbot.service.ChatNLPService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bkane on 5/4/18.
 */
@RestController
@RequestMapping(value = "/api")
public class ChatController {

    private static final Logger LOGGER = Logger.getLogger(ChatController.class);

    @Autowired
    private ChatSession chatSession;

    @Autowired
    private ChatNLPService chatNLPService;

    @RequestMapping(value = "/chats", method = RequestMethod.GET)
    public ResponseEntity<?> getChatHistory(HttpServletRequest request) {
        List<ChatData> chatDataList = chatNLPService.findAll();
        return new ResponseEntity<Object>(chatDataList, HttpStatus.OK);
    }

    @RequestMapping(value = "/chats/{chatSessionId}", method = RequestMethod.GET)
    public ResponseEntity<?> getChatsByChatSessionId(@PathVariable String chatSessionId, HttpServletRequest request) {
        // get the AUTH from the JWT token.
        List<ChatData> chatDataList = chatNLPService.findByChatSessionId(chatSessionId);
        return new ResponseEntity<Object>(chatDataList, HttpStatus.OK);
    }

    @RequestMapping(value = "/chats", method = RequestMethod.POST)
    public ResponseEntity<?> chatMessage(@RequestBody ChatData chatData, HttpServletRequest request) {
        LOGGER.debug(">>>> current session object = " + request.getSession());
        /*LOGGER.debug(">>>> current session Id = " + request.getSession().getId()
                + ", cookies = " + request.getCookies().length);*/
        final Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        chatSession.setAuthCode(chatData.getAuthCode());
        if ("Initiate".equals(chatData.getMessage())) {
            ChatData initiateResponse = chatNLPService.generateInitiateChatResponse(chatData, chatSession);
            initiateResponse.setCurrentSessionId(cookie);
            return new ResponseEntity<Object>(initiateResponse, HttpStatus.OK);
        }

        ChatData response = chatNLPService.generateChatBotResponse(chatData, chatSession);
        response.setCurrentSessionId(cookie);
        LOGGER.debug(">>>> Response Object = " + cookie);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }
}
