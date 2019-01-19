package com.seerlogics.botadmin.service;

import com.seerlogics.botadmin.dto.LaunchModel;
import com.seerlogics.botadmin.model.*;
import com.seerlogics.botadmin.repository.BotRepository;
import com.lingoace.exception.jpa.UnknownTypeException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.lingoace.util.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by bkane on 10/31/18.
 */
@Service
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
    private BotLauncher botLauncher;

    public Bot initModel(String type) {
        Collection<Category> categories = categoryService.getAll();
        Collection<Language> languages = languageService.getAll();
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
        return bot;
    }

    @Override
    public Collection<Bot> getAll() {
        return this.botRepository.findAll();
    }

    @Override
    public Bot save(Bot bot) {
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
        // get the bot and model to use
        Bot bot = this.botRepository.getOne(launchModel.getBot().getId());
        botLauncher.launchBotAsync(launchModel);
        return bot;
    }

    public Bot launchBotOld(LaunchModel launchModel) {
        String contextPath = "/chatbot";
        // get the bot and model to use
        Bot bot = this.botRepository.getOne(launchModel.getBot().getId());
        TrainedModel trainedModel = this.trainedModelService.getSingle(launchModel.getTrainedModelId());
        this.trainedModelService.writeModelToFile(trainedModel,
                "apps/chatbot/src/main/resources/nlp/models/custom/en-cat-eventgenie-intents-dynamic.bin");
        RunScript.runCommand("chmod +x /home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/clean.sh");
        RunScript.runCommand("/home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/clean.sh");
        RunScript.runCommand("chmod +x /home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/install.sh");
        RunScript.runCommand("/home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/install.sh");
        String freePort = String.valueOf(RunScript.getAFreePort());
        RunScript.runCommand("chmod +x /home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/launchBot.sh");
        RunScript.runCommandWithArgs("/home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/launchBot.sh",
                "--server.port=" + freePort, "--server.servlet.context-path=" + contextPath);
        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHED.name()));
        Configuration configuration = new Configuration();
        configuration.setTrainedModel(trainedModel);
        configuration.setEnvironment(Status.STATUS_CODES.LAUNCHED.name());
        configuration.setUrl("http://localhost:" + freePort + contextPath + "/api/chats");
        configuration.setPort(Integer.parseInt(freePort));
        bot.getConfigurations().add(configuration);
        this.save(bot);
        return bot;
    }

    public Bot stopBot(Long id) {
        Bot bot = this.botRepository.getOne(id);
        List<Configuration> configs = new ArrayList<>(bot.getConfigurations());
        RunScript.runCommand("chmod +x /home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/stopBot.sh");
        RunScript.runCommandWithArgs("/home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/stopBot.sh",
                String.valueOf(configs.get(0).getPort()));
        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.STOPPED.name()));
        bot.getConfigurations().clear();
        return this.save(bot);
    }
}
