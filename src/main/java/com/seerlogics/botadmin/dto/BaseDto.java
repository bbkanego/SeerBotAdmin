package com.seerlogics.botadmin.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseDto implements Serializable {
    private Map<String, Object> referenceData = new HashMap<>();

    public Map<String, Object> getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(Map<String, Object> referenceData) {
        this.referenceData = referenceData;
    }
}
