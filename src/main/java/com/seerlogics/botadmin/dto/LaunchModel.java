package com.seerlogics.botadmin.dto;

import com.seerlogics.botadmin.model.Bot;

public class LaunchModel extends BaseDto {
    private Bot bot;
    private Long trainedModelId;
    private String ownerUserName;

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public Long getTrainedModelId() {
        return trainedModelId;
    }

    public void setTrainedModelId(Long trainedModelId) {
        this.trainedModelId = trainedModelId;
    }
}
