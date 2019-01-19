package com.seerlogics.botadmin.model;

import com.lingoace.model.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by bkane on 11/24/18.
 */
@Entity
@Table(name = "configuration")
public class Configuration extends BaseModel {
    @Column(name = "url", length = 300)
    private String url;

    @Column(name = "port")
    private Integer port;

    @Column(name = "environment", length = 50)
    private String environment;

    @javax.persistence.ManyToOne(fetch = javax.persistence.FetchType.EAGER, optional = false)
    @javax.persistence.JoinColumn(name = "model_id", nullable = false)
    private TrainedModel trainedModel = new TrainedModel();

    // comma delimited string of instance ids
    @Column(length = 500)
    private String instanceIds;

    @Column(length = 500)
    private String imageIds;

    @Column(length = 500)
    private String publicDns;

    @Column(length = 500)
    private String publicIps;

    public String getPublicDns() {
        return publicDns;
    }

    public void setPublicDns(String publicDns) {
        this.publicDns = publicDns;
    }

    public String getPublicIps() {
        return publicIps;
    }

    public void setPublicIps(String publicIps) {
        this.publicIps = publicIps;
    }

    public String getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(String instanceIds) {
        this.instanceIds = instanceIds;
    }

    public String getImageIds() {
        return imageIds;
    }

    public void setImageIds(String imageIds) {
        this.imageIds = imageIds;
    }

    public TrainedModel getTrainedModel() {
        return trainedModel;
    }

    public void setTrainedModel(TrainedModel trainedModel) {
        this.trainedModel = trainedModel;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
