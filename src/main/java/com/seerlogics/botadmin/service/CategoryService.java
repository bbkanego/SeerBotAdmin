package com.seerlogics.botadmin.service;

import com.lingoace.common.exception.GeneralErrorException;
import com.lingoace.common.exception.NotAuthorizedException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.event.CategoryCreatedEvent;
import com.seerlogics.botadmin.exception.ErrorCodes;
import com.seerlogics.commons.dto.SearchBots;
import com.seerlogics.commons.dto.SearchIntents;
import com.seerlogics.commons.dto.SearchTrainedModel;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.Category;
import com.seerlogics.commons.model.Intent;
import com.seerlogics.commons.repository.BotRepository;
import com.seerlogics.commons.repository.CategoryRepository;
import com.seerlogics.commons.repository.IntentRepository;
import com.seerlogics.commons.repository.TrainedModelRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_OR_ADMIN_OR_USER_ROLE;

/**
 * Created by bkane on 11/1/18.
 */
@Service
@Transactional("botAdminTransactionManager")
@PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
public class CategoryService extends BaseServiceImpl<Category> {

    private final CategoryRepository categoryRepository;
    private final AccountService accountService;
    private final HelperService helperService;
    private final BotRepository botRepository;
    private final TrainedModelRepository trainedModelRepository;
    private final IntentRepository intentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CategoryService(CategoryRepository categoryRepository, AccountService accountService,
                           HelperService helperService, BotRepository botRepository,
                           TrainedModelRepository trainedModelRepository, IntentRepository intentRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.categoryRepository = categoryRepository;
        this.accountService = accountService;
        this.helperService = helperService;
        this.botRepository = botRepository;
        this.trainedModelRepository = trainedModelRepository;
        this.intentRepository = intentRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Collection<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Collection<Category> findForEdit() {
        List<Account> accounts = Arrays.asList(accountService.getAuthenticatedUser());
        List<String> catsToIgnore = new ArrayList<>();
        catsToIgnore.add("NONE");
        List<Category> categoryList = categoryRepository.findByOwnerAccounts(accounts, catsToIgnore);

        for (Category currentCategory : categoryList) {
            currentCategory.setDeleteAllowed(isAllowedToDelete(currentCategory));
        }

        return categoryList;
    }

    @PreAuthorize(HAS_UBER_ADMIN_OR_ADMIN_OR_USER_ROLE)
    public Collection<Category> findFilteredCategoriesAllForSelection() {
        Account loggedInUser = accountService.getAuthenticatedUser();
        Account admin = accountService.getAccountByUsername(this.helperService.getUberAdminAccountCode());
        List<Account> accounts = Arrays.asList(admin, loggedInUser);
        List<String> catsToIgnore = new ArrayList<>();
        if (!loggedInUser.getUserName().equals(this.helperService.getUberAdminAccountCode())) {
            catsToIgnore.add(this.helperService.getGenericCategoryCode());
        }
        return categoryRepository.findByOwnerAccounts(accounts, catsToIgnore);
    }

    @Override
    public Category getSingle(Long id) {
        Category category = categoryRepository.getOne(id);
        category.setDeleteAllowed(this.isAllowedToDelete(category));
        return category;
    }

    @Override
    public Category save(Category category) {
        if (StringUtils.isBlank(category.getCode())) {
            category.setCode("CAT-" + helperService.generateRandomCode());
        }
        // is new category?
        if (category.getId() == null) {
            category.setOwnerAccount(accountService.getAuthenticatedUser());
            Category createCategory = categoryRepository.save(category);

            LOGGER.debug("Creating Initiate intent for Category");
            Intent initiateIntent = IntentService.createStandardIntents(createCategory,
                    createCategory.getOwnerAccount(), "Initiate", "Initiate");
            intentRepository.save(initiateIntent);

            LOGGER.debug("Creating Salutation/HI intent for Category");
            Intent hiIntent = IntentService.createStandardIntents(createCategory,
                    createCategory.getOwnerAccount(), "Hi", "Hi");
            intentRepository.save(hiIntent);

            LOGGER.debug("Creating DoNotUnderstandIntent for Category");
            Intent doNotUnderstandIntent = IntentService.createStandardIntents(createCategory,
                    createCategory.getOwnerAccount(), "DoNotUnderstandIntent", "NONE");
            intentRepository.save(doNotUnderstandIntent);

            return createCategory;
        } else {
            Category tempCat = categoryRepository.getOne(category.getId());
            if (this.helperService.isAllowedToEdit(tempCat.getOwnerAccount())) {
                return categoryRepository.save(category);
            } else {
                throw new NotAuthorizedException(ErrorCodes.UNAUTHORIZED_ACCESS);
            }
        }
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.getOne(id);

        if (!isAllowedToDelete(category)) {
            GeneralErrorException generalErrorException = new GeneralErrorException();
            generalErrorException.addError("notAllowedToDeleteCategory",
                    this.helperService.getMessage("message.category.cannot.delete", null), null);
            throw generalErrorException;
        }

        if (this.helperService.isAllowedToEdit(category.getOwnerAccount())) {
            categoryRepository.delete(category);
        } else {
            throw new NotAuthorizedException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }
    }

    public boolean isAllowedToDelete(Category category) {
        SearchBots searchBots = new SearchBots();
        searchBots.setCategory(category);
        searchBots.setOwnerAccount(this.accountService.getAuthenticatedUser());

        SearchTrainedModel searchTrainedModel = new SearchTrainedModel();
        searchTrainedModel.setCategory(category);
        searchTrainedModel.setOwnerAccount(this.accountService.getAuthenticatedUser());

        SearchIntents searchIntents = new SearchIntents();
        searchIntents.setCategory(category);
        searchIntents.setOwnerAccount(this.accountService.getAuthenticatedUser());

        return this.botRepository.findBots(searchBots).isEmpty()
                && this.intentRepository.findIntentsAndUtterances(searchIntents).isEmpty()
                && this.trainedModelRepository.findTrainedModel(searchTrainedModel).isEmpty();
    }

    public Category initModel() {
        return new Category();
    }

    public Category getCategoryByCode(String code) {
        return categoryRepository.findByCode(code);
    }
}
