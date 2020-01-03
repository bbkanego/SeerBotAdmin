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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE;

@RestController
@RequestMapping(value = "/api/v1/model")
@PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
public class TrainedModelController extends BaseController implements CrudController<TrainedModel> {
    @Autowired
    private TrainedModelService trainedModelService;

    @GetMapping(value = "/train/init/{modelType}")
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
    public Boolean trainModel(@Validate("validateTrainModelRule") @RequestBody TrainedModel trainedModel) {
        this.trainedModelService.trainModel(trainedModel);
        return true;
    }

    @GetMapping(value = "/re-train/{id}")
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
    public Boolean save(TrainedModel object) {
        this.trainedModelService.save(object);
        return true;
    }

    @Override
    @GetMapping(value = {"", "/"})
    public Collection<TrainedModel> getAll() {
        return this.trainedModelService.findModelByOwner();
    }

    @Override
    @GetMapping(value = {"/{id}"})
    public TrainedModel getById(@PathVariable("id") Long id) {
        return this.trainedModelService.getModelForUpdate(id);
    }

    @Override
    @DeleteMapping(value = {"/{id}"})
    public Boolean delete(@PathVariable("id") Long id) {
        this.trainedModelService.delete(id);
        return true;
    }
}
