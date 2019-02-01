package com.seerlogics.botadmin.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseDto<T> implements Serializable {
    private Map<String, Object> referenceData = new HashMap<>();
    private Set<T> searchResults = new HashSet<>();

    public Map<String, Object> getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(Map<String, Object> referenceData) {
        this.referenceData = referenceData;
    }

    public Set<T> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(Set<T> searchResults) {
        this.searchResults = searchResults;
    }
}
