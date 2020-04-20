package com.seerlogics.botadmin.service;

import com.lingoace.common.exception.GeneralErrorException;
import com.lingoace.exception.nlp.TrainModelException;
import com.lingoace.nlp.opennlp.NLPModelTrainer;
import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.dto.SearchTrainedModel;
import com.seerlogics.commons.model.Intent;
import com.seerlogics.commons.model.IntentUtterance;
import com.seerlogics.commons.model.TrainedModel;
import com.seerlogics.commons.repository.LaunchInfoRepository;
import com.seerlogics.commons.repository.TrainedModelRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE;

@Service
@Transactional("botAdminTransactionManager")
@PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
public class TrainedModelService extends BaseServiceImpl<TrainedModel> {
    private final TrainedModelRepository trainedModelRepository;

    private final AccountService accountService;

    private final IntentService intentService;

    private final CategoryService categoryService;

    private final LaunchInfoRepository launchInfoRepository;

    private final HelperService helperService;

    public TrainedModelService(TrainedModelRepository trainedModelRepository, AccountService accountService,
                               IntentService intentService, CategoryService categoryService,
                               LaunchInfoRepository launchInfoRepository, HelperService helperService) {
        this.trainedModelRepository = trainedModelRepository;
        this.accountService = accountService;
        this.intentService = intentService;
        this.categoryService = categoryService;
        this.launchInfoRepository = launchInfoRepository;
        this.helperService = helperService;
    }

    @Override
    public Collection<TrainedModel> getAll() {
        return trainedModelRepository.findAll();
    }

    public Collection<TrainedModel> findModelsByOwner() {
        return trainedModelRepository.findByOwner(accountService.getAuthenticatedUser());
    }

    public Collection<TrainedModel> findModels(SearchTrainedModel searchTrainedModel) {
        return trainedModelRepository.findTrainedModel(searchTrainedModel);
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
        TrainedModel trainedModel = this.getSingle(id);
        if (!this.launchInfoRepository.findByTrainedModel(trainedModel).isEmpty()) {
            GeneralErrorException generalErrorException = new GeneralErrorException();
            generalErrorException.addError("cannotDeleteModel",
                    this.helperService.getMessage("message.trainedModel.cannot.delete",
                            null), null);
            throw generalErrorException;
        }
        trainedModelRepository.delete(trainedModel);
    }

    @Override
    public TrainedModel findByCode(String code) {
        return trainedModelRepository.findByName(code);
    }

    public void trainModel(TrainedModel trainedModel) {
        /**
         * get all the standard intents for the category
         */
        createModelFromUtterances(trainedModel);
        trainedModel.setOwner(accountService.getAuthenticatedUser());
        trainedModelRepository.save(trainedModel);
    }

    private void createModelFromUtterances(TrainedModel trainedModel) {
        StringBuilder buffer = new StringBuilder();
        List<Intent> intents
                = this.intentService.findIntentsByCategoryTypeAndOwner(trainedModel.getCategory().getCode(),
                trainedModel.getType());
        for (Intent currentIntent : intents) {
            Set<IntentUtterance> intentUtterances = currentIntent.getUtterances();
            for (IntentUtterance intentUtterance : intentUtterances) {
                buffer.append(currentIntent.getIntent());
                buffer.append(" ");
                buffer.append(intentUtterance.getUtterance());
                buffer.append(System.lineSeparator());
            }
        }
        LOGGER.debug("-------------------------------");
        LOGGER.debug("Trained Model buffer = {}", buffer);
        LOGGER.debug("-------------------------------");
        ByteArrayOutputStream outStream = NLPModelTrainer.trainDoccatModel(buffer);
        trainedModel.setFile(outStream.toByteArray());
    }

    public void reTrainModel(Long existingModelId) {
        // get existing trained model
        TrainedModel existingTrainedModel = this.trainedModelRepository.getOne(existingModelId);
        /**
         * get all the standard intents for the category
         */
        createModelFromUtterances(existingTrainedModel);
        trainedModelRepository.save(existingTrainedModel);
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
        trainedModel.getReferenceData().put("categories", this.categoryService.findFilteredCategoriesAllForSelection());
        return trainedModel;
    }

    public TrainedModel getModelForUpdate(Long id) {
        TrainedModel trainedModel = this.getSingle(id);
        // now check if there are any bots using this model.
        if (this.launchInfoRepository.findByTrainedModel(trainedModel).isEmpty()) {
            trainedModel.setDeleteAllowed(true);
        }
        return trainedModel;
    }
}
