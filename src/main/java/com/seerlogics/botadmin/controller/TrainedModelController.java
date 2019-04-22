package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.lingoace.spring.controller.CrudController;
import com.lingoace.validation.Validate;
import com.seerlogics.botadmin.service.TrainedModelService;
import com.seerlogics.commons.model.TrainedModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/model")
public class TrainedModelController extends BaseController implements CrudController<TrainedModel> {
    @Autowired
    private TrainedModelService trainedModelService;

    @GetMapping(value = "/train/init/{modelType}")
    @ResponseBody
    public TrainedModel initTrainModel(@PathVariable("modelType") String modelType) {
        return trainedModelService.initTrainedModel(modelType);
    }

    /**
     * This will get utterance for a specific category and train the model for you.
     *
     * @param trainedModel
     * @return
     */
    @PostMapping(value = "/train")
    @ResponseBody
    public Boolean trainModel(@Validate("validateTrainModelRule") @RequestBody TrainedModel trainedModel) {
        this.trainedModelService.trainModel(trainedModel);
        return true;
    }

    @GetMapping(value = "/re-train/{id}")
    @ResponseBody
    public Boolean reTrainModel(@PathVariable Long id) {
        this.trainedModelService.reTrainModel(id);
        return true;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        // Load file from database
        TrainedModel trainedModel = trainedModelService.getSingle(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + trainedModel.getName() + "\"")
                .body(new ByteArrayResource(trainedModel.getFile()));
    }

    @GetMapping("/write/{id}")
    public void writeModel(@PathVariable Long id) {
        // Load file from database
        TrainedModel trainedModel = trainedModelService.getSingle(id);
        trainedModelService.writeModelToFile(trainedModel,
                "apps/chatbot/src/main/resources/nlp/models/custom/en-cat-eventgenie-intents-dynamic.bin");
    }

    @Override
    @PostMapping(value = {"", "/"})
    @ResponseBody
    public Boolean save(TrainedModel object) {
        this.trainedModelService.save(object);
        return true;
    }

    @Override
    @GetMapping(value = {"", "/"})
    @ResponseBody
    public Collection<TrainedModel> getAll() {
        return this.trainedModelService.findModelByOwner();
    }

    @Override
    @GetMapping(value = {"/{id}"})
    @ResponseBody
    public TrainedModel getById(@PathVariable("id") Long id) {
        return this.trainedModelService.getSingle(id);
    }

    @Override
    @DeleteMapping(value = {"/{id}"})
    @ResponseBody
    public Boolean delete(@PathVariable("id") Long id) {
        this.trainedModelService.delete(id);
        return true;
    }
}
