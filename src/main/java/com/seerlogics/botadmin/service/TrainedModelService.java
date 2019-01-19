package com.seerlogics.botadmin.service;

import com.seerlogics.botadmin.model.PredefinedIntentUtterances;
import com.seerlogics.botadmin.model.TrainedModel;
import com.seerlogics.botadmin.repository.TrainedModelRepository;
import com.lingoace.exception.nlp.TrainModelException;
import com.lingoace.nlp.opennlp.NLPModelTrainer;
import com.lingoace.spring.service.BaseServiceImpl;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

@Service
public class TrainedModelService extends BaseServiceImpl<TrainedModel> {
    @Autowired
    private TrainedModelRepository trainedModelRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PredefinedIntentService predefinedIntentService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Collection<TrainedModel> getAll() {
        return trainedModelRepository.findAll();
    }

    @Override
    public TrainedModel getSingle(Long id) {
        return trainedModelRepository.getOne(id);
    }

    @Override
    public TrainedModel save(TrainedModel object) {
        return trainedModelRepository.save(object);
    }

    @Override
    public List<TrainedModel> saveAll(List<TrainedModel> object) {
        return trainedModelRepository.saveAll(object);
    }

    @Override
    public void delete(Long id) {
        trainedModelRepository.deleteById(id);
    }

    @Override
    public TrainedModel findByCode(String code) {
        return trainedModelRepository.findByName(code);
    }

    public void trainModel(TrainedModel trainedModel) {
        /**
         * get all the standard intents for the category
         * //todo include the custom intents also here.
         */
        List<PredefinedIntentUtterances> predefinedIntentUtterancesList
                = this.predefinedIntentService.findIntentsByCategory(trainedModel.getCategory().getCode());
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < predefinedIntentUtterancesList.size(); i++) {
            PredefinedIntentUtterances predefinedIntentUtterances = predefinedIntentUtterancesList.get(i);
            buffer.append(predefinedIntentUtterances.getIntent());
            buffer.append(" ");
            buffer.append(predefinedIntentUtterances.getUtterance());
            if (i < (predefinedIntentUtterancesList.size() - 1)) {
                buffer.append(System.lineSeparator());
            }
        }
        ByteArrayOutputStream outStream = NLPModelTrainer.trainDoccatModel(buffer);
        trainedModel.setFile(outStream.toByteArray());
        trainedModel.setOwner(accountService.getAuthenticatedUser());
        trainedModelRepository.save(trainedModel);
        //---writeModelToFile(trainedModel2, "apps/chatbot/src/main/resources/nlp/models/custom/test.bin");
    }

    void writeModelById(Long id, String fileToWrite) {
        TrainedModel trainedModel = this.trainedModelRepository.getOne(id);
        writeModelToFile(trainedModel, fileToWrite);
    }

    public void writeModelToFile(TrainedModel trainedModel, String fileToWrite) {
        try {
            URL fileUrl = null;
            try {
                fileUrl = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + fileToWrite);
                FileUtils.writeByteArrayToFile(new File(fileUrl.toString().replace("file:", "").trim()), trainedModel.getFile());
            } catch(FileNotFoundException e) {
                File newFile = new File(ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX).toString()
                        .replace("file:", "").trim() + fileToWrite);
                if(newFile.createNewFile()) {
                    FileUtils.writeByteArrayToFile(newFile, trainedModel.getFile());
                }
            }
            /*File target = new File(ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + fileToWrite).toURI());
            boolean fileCreated = false;
            if (!target.exists()) {
                fileCreated = target.createNewFile();
            } else {
                fileCreated = true;
            }
            if (fileCreated) {
                FileUtils.writeByteArrayToFile(target, trainedModel.getFile());
            }*/
        } catch (Exception e) {
            throw new TrainModelException(e);
        }
    }

    public TrainedModel initTrainedModel() {
        TrainedModel trainedModel = new TrainedModel();
        trainedModel.getReferenceData().put("categories", this.categoryService.getAll());
        return trainedModel;
    }
}
