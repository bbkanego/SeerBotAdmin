package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.util.RunScript;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.event.InstanceLaunchedEvent;
import com.seerlogics.botadmin.service.AccountService;
import com.seerlogics.botadmin.service.BotService;
import com.seerlogics.botadmin.service.TrainedModelService;
import com.seerlogics.commons.dto.LaunchModel;
import com.seerlogics.commons.dto.SearchBots;
import com.seerlogics.commons.dto.SearchTrainedModel;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.Bot;
import com.seerlogics.commons.model.Configuration;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by bkane on 11/1/18.
 */
@RestController
@RequestMapping(value = "/api/v1/chatbot")
public class BotController extends BaseController implements ApplicationListener<ApplicationEvent> {
    private final BotService botService;

    private final TrainedModelService trainedModelService;

    private final AccountService accountService;

    public BotController(BotService botService, TrainedModelService trainedModelService, AccountService accountService) {
        this.botService = botService;
        this.trainedModelService = trainedModelService;
        this.accountService = accountService;
    }

    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@Validate("validateBotRule") @RequestBody Bot chatBot) {
        this.botService.save(chatBot);
        return true;
    }

    @GetMapping(value = {"", "/",})
    @ResponseBody
    public Collection<Bot> getAll() {
        return this.botService.getAll();
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        this.botService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public Bot get(@PathVariable("id") Long id) {
        return this.botService.getSingle(id);
    }

    @GetMapping(value = "/init/{type}")
    @ResponseBody
    public Bot initModel(@PathVariable("type") String type) {
        return botService.initModel(type);
    }

    @GetMapping(value = "/launch/start/{id}")
    @ResponseBody
    public LaunchModel startLaunch(@PathVariable("id") Long botId) {
        LaunchModel launchModel = new LaunchModel();
        Bot targetBot = this.botService.getSingle(botId);
        launchModel.setBot(targetBot);
        SearchTrainedModel searchTrainedModel = new SearchTrainedModel();
        searchTrainedModel.setCategory(targetBot.getCategory());
        searchTrainedModel.setOwnerAccount(this.accountService.getAuthenticatedUser());
        launchModel.getReferenceData().put("trainedModels", this.trainedModelService.findModels(searchTrainedModel));
        return launchModel;
    }

    @PostMapping(value = "/test")
    @ResponseBody
    public Map<String, Object> test(@Validate("validateLaunchBotRule") @RequestBody LaunchModel launchModel) {
        Bot bot = this.botService.testBot(launchModel);
        return getBotConfiguration(bot);
    }

    @GetMapping(value = "/launch/{id}")
    @ResponseBody
    public Map<String, Object> launch(@PathVariable Long id) {
        Bot bot = this.botService.launchBot(id);
        return getBotConfiguration(bot);
    }

    private Map<String, Object> getBotConfiguration(Bot bot) {
        Map<String, Object> returnData = new HashMap<>();
        returnData.put("bot", bot);
        List<Configuration> configurations = new ArrayList<>(bot.getConfigurations());
        for (int i = 0; i < configurations.size(); i++) {
            Configuration configuration = configurations.get(i);
            returnData.put("url" + (i + 1), configuration.getUrl());
            returnData.put("uniqueBotId" + (i + 1), configuration.getUniqueBotId());
        }
        return returnData;
    }

    @GetMapping(value = "/stop/{id}")
    @ResponseBody
    public Bot stop(@PathVariable("id") Long botId) {
        return this.botService.stopBot(botId);
    }

    @GetMapping(value = "/restart/{id}")
    @ResponseBody
    public Bot restart(@PathVariable("id") Long botId) {
        return this.botService.restartBot(botId);
    }

    @GetMapping(value = "/terminate/{id}")
    @ResponseBody
    public Boolean terminate(@PathVariable("id") Long botId) {
        this.botService.delete(botId);
        // https://askubuntu.com/questions/346394/how-to-write-a-shscript-to-kill-9-a-pid-which-is-found-via-lsof-i
        RunScript.runCommand("chmod +x /home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/terminateBot.sh");
        RunScript.runCommand("/home/bkane/svn/code/java/BotAdmin/src/main/resources/scripts/terminateBot.sh");
        return true;
    }

    @GetMapping(value = "/status/{id}/{status}")
    @ResponseBody
    public Bot changeStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return botService.changeStatus(id, status);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof InstanceLaunchedEvent) {
            LOGGER.debug("Instance has been launched!!");
        }
    }

    @GetMapping(value = "/search/init")
    @ResponseBody
    public SearchBots initSearchBots() {
        return botService.initSearchBots();
    }

    @PostMapping(value = "/search")
    @ResponseBody
    public List<Bot> searchBots(@Validate("validateSearchBotsRule") @RequestBody SearchBots searchBots) {
        Account account = accountService.getAuthenticatedUser();
        searchBots.setOwnerAccount(account);
        return botService.findBots(searchBots);
    }
}
