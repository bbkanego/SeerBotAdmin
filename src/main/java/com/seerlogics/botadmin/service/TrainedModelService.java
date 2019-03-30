package com.seerlogics.botadmin.service;

import com.lingoace.exception.nlp.TrainModelException;
import com.lingoace.nlp.opennlp.NLPModelTrainer;
import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.Intent;
import com.seerlogics.commons.model.IntentUtterance;
import com.seerlogics.commons.model.TrainedModel;
import com.seerlogics.commons.repository.TrainedModelRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TrainedModelService extends BaseServiceImpl<TrainedModel> {
    @Autowired
    private TrainedModelRepository trainedModelRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private IntentService intentService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Collection<TrainedModel> getAll() {
        return trainedModelRepository.findAll();
    }

    public Collection<TrainedModel> findModelByOwner() {
        return trainedModelRepository.findByOwner(accountService.getAuthenticatedUser());
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
    public List<TrainedModel> saveAll(Collection<TrainedModel> object) {
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
         */
        StringBuilder buffer = new StringBuilder();
        List<Intent> intents
                = this.intentService.findIntentsByCategoryTypeAndOwner(trainedModel.getCategory().getCode(),
                trainedModel.getType());
        for (Intent currentIntent : intents) {
            Set<IntentUtterance> intentUtterances = currentIntent.getUtterances();
            int j = 0;
            for (IntentUtterance intentUtterance : intentUtterances) {
                j++;
                buffer.append(currentIntent.getIntent());
                buffer.append(" ");
                buffer.append(intentUtterance.getUtterance());
                if (j < (intentUtterances.size() - 1)) {
                    buffer.append(System.lineSeparator());
                }
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
            } catch (FileNotFoundException e) {
                File newFile = new File(ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX).toString()
                        .replace("file:", "").trim() + fileToWrite);
                if (newFile.createNewFile()) {
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

    public TrainedModel initTrainedModel(String modelType) {
        TrainedModel trainedModel = new TrainedModel();
        if (TrainedModel.MODEL_TYPE.valueOf(modelType.toUpperCase()) == null) {
            throw new TrainModelException("The model type defined is incorrect: " + modelType);
        }
        trainedModel.setType(modelType.toUpperCase());
        trainedModel.getReferenceData().put("categories", this.categoryService.getAll());
        return trainedModel;
    }
}
