package com.seerlogics.botadmin.controller;

import com.seerlogics.botadmin.dto.LaunchModel;
import com.seerlogics.botadmin.event.InstanceLaunchedEvent;
import com.seerlogics.botadmin.model.Bot;
import com.seerlogics.botadmin.model.Configuration;
import com.seerlogics.botadmin.service.AccountService;
import com.seerlogics.botadmin.service.BotService;
import com.seerlogics.botadmin.service.TrainedModelService;
import com.lingoace.spring.controller.BaseController;
import com.lingoace.util.RunScript;
import com.lingoace.validation.Validate;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private BotService botService;

    @Autowired
    private TrainedModelService trainedModelService;

    @Autowired
    private AccountService accountService;

    @PostMapping(value = {"", "/",})
    @ResponseBody
    public Boolean save(@Validate("validateBotRule") @RequestBody Bot chatBot) {
        chatBot.setOwner(accountService.getAuthenticatedUser());
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
    public String delete(@PathVariable("id") Long id) {
        this.botService.delete(id);
        return "success";
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
        launchModel.setBot(this.botService.getSingle(botId));
        launchModel.getReferenceData().put("trainedModels", this.trainedModelService.getAll());
        return launchModel;
    }

    @PostMapping(value = "/launch")
    @ResponseBody
    public Map<String, Object> launch(@Validate("validateLaunchBotRule") @RequestBody LaunchModel launchModel) {
        launchModel.setOwnerUserName(accountService.getAuthenticatedUser().getUserName());
        Bot bot = this.botService.launchBot(launchModel);
        Map<String, Object> returnData = new HashMap<>();
        returnData.put("bot", bot);
        List<Configuration> configurations = new ArrayList<>(bot.getConfigurations());
        for (int i = 0; i < configurations.size(); i++) {
            Configuration configuration = configurations.get(i);
            returnData.put("url" + (i + 1), configuration.getUrl());
        }
        return returnData;
    }

    @GetMapping(value = "/stop/{id}")
    @ResponseBody
    public Bot stop(@PathVariable("id") Long botId) {
        return this.botService.stopBot(botId);
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
}