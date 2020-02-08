package com.seerlogics.botadmin.service;

import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.ReferenceData;
import com.seerlogics.commons.model.Role;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class HelperService {

    private final AccountService accountService;
    private final RandomStringGenerator generator;

    @Resource(name = "appMessageResource")
    private MessageSource messageSource;

    @Value("${seerapp.generic.category.code}")
    private String genericCategoryCode;

    @Value("${seerapp.uber.admin.account.code:admin}")
    private String uberAdminAccountCode;

    public String getUberAdminAccountCode() {
        return uberAdminAccountCode;
    }

    public String getGenericCategoryCode() {
        return genericCategoryCode;
    }

    public HelperService(AccountService accountService) {
        this.accountService = accountService;

        generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();
    }

    boolean isAllowedToEdit(Account target) {
        Account currentUser = accountService.getAuthenticatedUser();
        return isAllowedFullAccess(currentUser) || target.getId().equals(currentUser.getId());
    }

    static boolean isAllowedFullAccess(Account target) {
        return target.getRoles().stream().
                anyMatch(role -> role.getCode().equals(Role.ROLE_TYPE.UBER_ADMIN.name()));
    }

    String generateRandomCode() {
        return generator.generate(20);
    }

    List<Map<String, String>> buildReferenceData(Collection<? extends ReferenceData> referenceDataCollection) {
        List<Map<String, String>> referenceDataList = new ArrayList<>();
        for (ReferenceData referenceData : referenceDataCollection) {
            Map<String, String> localesMap = new HashMap<>();
            localesMap.put("code", referenceData.getCode());
            localesMap.put("name", referenceData.getName());
            referenceDataList.add(localesMap);
        }
        return referenceDataList;
    }

    public String getMessage(String messagekey, String[] arguments) {
        return messageSource.getMessage(messagekey, arguments, Locale.getDefault());
    }
}
