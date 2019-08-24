package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.exception.ErrorCodes;
import com.seerlogics.commons.exception.BaseRuntimeException;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.Category;
import com.seerlogics.commons.repository.CategoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by bkane on 11/1/18.
 */
@Service
@Transactional
@PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
public class CategoryService extends BaseServiceImpl<Category> {

    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    public CategoryService(CategoryRepository categoryRepository, AccountService accountService) {
        this.categoryRepository = categoryRepository;
        this.accountService = accountService;
    }

    @Override
    public Collection<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Collection<Category> findForEdit(Category category) {
        return categoryRepository.findByOwnerAccounts(accountService.getAuthenticatedUser());
    }

    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN', 'ACCT_USER')")
    public Collection<Category> finaAllForSelection() {
        Account admin = accountService.getAccountByUsername("admin");
        return categoryRepository.findByOwnerAccounts(admin, accountService.getAuthenticatedUser());
    }

    @Override
    public Category getSingle(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public Category save(Category category) {
        if (StringUtils.isBlank(category.getCode())) {
            category.setCode("CAT-" + UUID.randomUUID());
        }
        // is new category?
        if (category.getId() == null) {
            category.setOwnerAccount(accountService.getAuthenticatedUser());
            return categoryRepository.save(category);
        } else {
            Category tempCat = categoryRepository.getOne(category.getId());
            if (tempCat.getOwnerAccount().getId().equals(accountService.getAuthenticatedUser().getId())) {
                return categoryRepository.save(category);
            } else {
                throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
            }
        }
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.getOne(id);
        if (accountService.getAuthenticatedUser().getId().equals(category.getOwnerAccount().getId())) {
            categoryRepository.delete(category);
        } else {
            throw new BaseRuntimeException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }
    }

    public Category initModel() {
        return new Category();
    }

    public Category getCategoryByCode(String code) {
        return categoryRepository.findByCode(code);
    }
}
