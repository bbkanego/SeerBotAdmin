package com.seerlogics.botadmin.service;

import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.ReferenceData;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HelperService {

    private final AccountService accountService;
    private final RandomStringGenerator generator;

    public HelperService(AccountService accountService) {
        this.accountService = accountService;

        generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();
    }

    boolean isAllowedToEdit(Account target) {
        return target.getId().equals(accountService.getAuthenticatedUser().getId());
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
}
