package com.seerlogics.botadmin.service;

import com.lingoace.exception.jpa.UnknownTypeException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.dto.LaunchModel;
import com.seerlogics.commons.dto.SearchBots;
import com.seerlogics.commons.model.*;
import com.seerlogics.commons.repository.BotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bkane on 10/31/18.
 */
@Service
@Transactional
public class BotService extends BaseServiceImpl<Bot> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private TrainedModelService trainedModelService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BotLauncher botLauncher;

    public Bot initModel(String type) {
        Collection<Category> categories = categoryService.getAll();
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
        bot.setOwner(accountService.getAuthenticatedUser());
        return this.botRepository.save(bot);
    }

    @Override
    public void delete(Long id) {
        this.botRepository.deleteById(id);
    }

    @Override
    public Bot getSingle(Long id) {
        Bot bot = this.botRepository.getOne(id);
        Collection<Category> categories = categoryService.getAll();
        Collection<Language> languages = languageService.getAll();
        bot.getReferenceData().put("categories", categories);
        bot.getReferenceData().put("languages", languages);
        return bot;
    }

    public Bot changeStatus(Long id, String code) {
        Bot bot = this.getSingle(id);
        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.valueOf(code).name()));
        return save(bot);
    }

    public Bot launchBot(LaunchModel launchModel) {
        Bot bot = this.botRepository.getOne(launchModel.getBot().getId());
        botLauncher.launchBotAsync(launchModel);
        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHING.name()));
        return save(bot);
    }

    public Bot stopBot(Long id) {
        Bot bot = this.botRepository.getOne(id);
        botLauncher.stopBotAsync(id);
        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.DRAFT.name()));
        return save(bot);
    }

    public Bot restartBot(Long id) {
        Bot bot = this.botRepository.getOne(id);
        botLauncher.restartBotAsync(id);
        return bot;
    }

    public SearchBots initSearchBots() {
        SearchBots searchBots = new SearchBots();
        searchBots.getReferenceData().put("category", categoryService.getAll());
        return searchBots;
    }

    public List<Bot> findBots(SearchBots searchBots) {
        return this.botRepository.findBots(searchBots);
    }
}
