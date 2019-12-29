package com.seerlogics.botadmin.service;

import com.lingoace.common.exception.GeneralErrorException;
import com.lingoace.exception.jpa.UnknownTypeException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.config.AppProperties;
import com.seerlogics.botadmin.exception.ErrorCodes;
import com.seerlogics.commons.dto.LaunchModel;
import com.seerlogics.commons.dto.SearchBots;
import com.seerlogics.commons.exception.BaseRuntimeException;
import com.seerlogics.commons.model.*;
import com.seerlogics.commons.repository.BotRepository;
import com.seerlogics.commons.repository.LaunchInfoRepository;
import com.seerlogics.commons.repository.TrainedModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by bkane on 10/31/18.
 */
@Service
@Transactional("botAdminTransactionManager")
@PreAuthorize("hasAnyRole('ACCT_ADMIN')")
public class BotService extends BaseServiceImpl<Bot> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);

    private final BotRepository botRepository;

    private final CategoryService categoryService;

    private final StatusService statusService;

    private final LanguageService languageService;

    private final AccountService accountService;

    private final TrainedModelRepository trainedModelRepository;

    private final BotLauncher botLauncher;

    private final HelperService helperService;

    private final LaunchInfoRepository launchInfoRepository;

    private final RestTemplate restTemplate;

    private AppProperties appProperties;

    @Value("${app.botType:simple}")
    private String botType;

    @Value(("${seerapp.chatbot.reinit.url:http://localhost:8099/chatbot/api/chats/re-init}"))
    private String chatbotReInitUrl;

    @Value(("${seerapp.authCode:2478360d-530d-4435-bf49-bf07c0e7e35b}"))
    private String authCode;

    public BotService(BotRepository botRepository, CategoryService categoryService,
                      StatusService statusService, LanguageService languageService,
                      AccountService accountService, TrainedModelRepository trainedModelRepository,
                      BotLauncher botLauncher, HelperService helperService, LaunchInfoRepository launchInfoRepository, RestTemplate restTemplate, AppProperties appProperties) {
        this.botRepository = botRepository;
        this.categoryService = categoryService;
        this.statusService = statusService;
        this.languageService = languageService;
        this.accountService = accountService;
        this.trainedModelRepository = trainedModelRepository;
        this.botLauncher = botLauncher;
        this.helperService = helperService;
        this.launchInfoRepository = launchInfoRepository;
        this.restTemplate = restTemplate;
        this.appProperties = appProperties;
    }

    public Bot initModel(String type) {
        Collection<Category> categories = categoryService.findFilteredCategoriesAllForSelection();
        Collection<Language> languages = languageService.getAll();
        Collection<Status> statuses = statusService.getAll();
        Bot bot = null;
        if (Bot.BOT_TYPE.CHAT_BOT.name().toLowerCase().equals(type)) {
            bot = new ChatBot();
        } else if (Bot.BOT_TYPE.VOICE_BOT.name().toLowerCase().equals(type)) {
            bot = new VoiceBot();
        } else {
            throw new UnknownTypeException("Unknown type = " + type);
        }
        bot.getReferenceData().put("categories", categories);
        bot.getReferenceData().put("languages", languages);
        bot.getReferenceData().put("statuses", statuses);

        List<Status> draftStatus = statuses.stream().filter(status -> status.getCode().equals(Status.STATUS_CODES.DRAFT.name()))
                .collect(Collectors.toList());
        bot.setStatus(draftStatus.get(0));

        return bot;
    }

    @Override
    public Collection<Bot> getAll() {
        return this.botRepository.findAll();
    }

    @Override
    public Bot save(Bot bot) {
        if (bot.getId() == null) {
            bot.setOwner(accountService.getAuthenticatedUser());
        } else {
            if (!this.helperService.isAllowedToEdit(bot.getOwner())) {
                throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
            }
        }
        return this.botRepository.save(bot);
    }

    @Override
    public void delete(Long id) {
        this.botRepository.deleteById(id);
    }

    @Override
    public Bot getSingle(Long id) {
        Bot bot = this.botRepository.getOne(id);

        if (!this.helperService.isAllowedToEdit(bot.getOwner())) {
            throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }

        Collection<Category> categories = categoryService.findFilteredCategoriesAllForSelection();
        Collection<Language> languages = languageService.getAll();
        bot.getReferenceData().put("categories", categories);
        bot.getReferenceData().put("languages", languages);
        return bot;
    }

    public Bot changeStatus(Long id, String code) {
        Bot bot = this.getSingle(id);
        if (!this.helperService.isAllowedToEdit(bot.getOwner())) {
            throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }
        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.valueOf(code).name()));
        return save(bot);
    }

    public Bot testBot(LaunchModel launchModel) {
        launchModel.setOwnerUserName(accountService.getAuthenticatedUser().getUserName());

        Bot targetBot = this.botRepository.getOne(launchModel.getBot().getId());

        if (!this.helperService.isAllowedToEdit(targetBot.getOwner())) {
            throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }

        if ("async".equals(this.botType)) {
            botLauncher.launchBotAsync(launchModel);
            targetBot.setStatus(statusService.findByCode(Status.STATUS_CODES.TESTING.name()));
        } else {
            launchBotSimple(launchModel, targetBot);
            targetBot.setStatus(statusService.findByCode(Status.STATUS_CODES.TESTING.name()));
        }
        return save(targetBot);
    }

    public Bot launchBot(Long id) {
        Bot targetBot = this.botRepository.getOne(id);

        if (!this.helperService.isAllowedToEdit(targetBot.getOwner())) {
            throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }

        targetBot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHED.name()));
        return save(targetBot);
    }

    private void launchBotSimple(LaunchModel launchModel, Bot targetBot) {
        LaunchInfo launchInfo = new LaunchInfo();
        launchInfo.setTargetBotId(targetBot.getId());
        launchInfo.setTrainedModel(trainedModelRepository.getOne(launchModel.getTrainedModelId()));
        launchInfo.setUniqueBotId(UUID.randomUUID().toString());
        launchInfo.setAllowedOrigins(launchModel.getAllowedOrigins());
        launchInfo.setChatUrl(appProperties.getChatAppDomain() + appProperties.getElbHealthCheckUrl());
        targetBot.getLaunchInfo().add(launchInfo);
    }

    public Bot stopBot(Long id) {
        Bot bot = this.botRepository.getOne(id);

        if (!this.helperService.isAllowedToEdit(bot.getOwner())) {
            throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }

        //botLauncher.stopBotAsync(id);
        LaunchInfo launchInfo = (LaunchInfo) bot.getLaunchInfo().toArray()[0];
        //bot.getLaunchInfo().remove(launchInfo);
        bot.getLaunchInfo().clear();
        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.DRAFT.name()));
        return save(bot);
    }

    public Bot restartBot(Long id) {
        Bot bot = this.botRepository.getOne(id);

        if (!this.helperService.isAllowedToEdit(bot.getOwner())) {
            throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }

        LaunchInfo launchInfo = this.launchInfoRepository.findByTargetBotId(bot.getId());
        String uniqueBotId = launchInfo.getUniqueBotId();

        ResponseEntity<String> response
                = this.restTemplate.getForEntity(this.chatbotReInitUrl + "/" + this.authCode + "/"
                        + uniqueBotId, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            GeneralErrorException generalErrorException = new GeneralErrorException();
            generalErrorException.addError(ErrorCodes.BOT_RE_INITIALIZATION_FAILED,
                    "Bot re-initialization failed", null);
            throw generalErrorException;
        }

        return bot;
    }

    public SearchBots initSearchBots() {
        SearchBots searchBots = new SearchBots();
        searchBots.getReferenceData().put("category", categoryService.findFilteredCategoriesAllForSelection());
        return searchBots;
    }

    public List<Bot> findBots(SearchBots searchBots) {
        return this.botRepository.findBots(searchBots);
    }
}
