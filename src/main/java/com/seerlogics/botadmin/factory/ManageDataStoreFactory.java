package com.seerlogics.botadmin.factory;

import com.seerlogics.botadmin.config.AppProperties;
import com.seerlogics.cloud.ManageDataStore;
import com.seerlogics.cloud.aws.s3.ManageDataStoreImpl;
import com.seerlogics.cloud.common.CloudConstant;

/**
 * Created by bkane on 4/26/19.
 */
public class ManageDataStoreFactory {
    public ManageDataStore createDataStoreManager(AppProperties appProperties) {
        if (CloudConstant.CloudEnvironment.AWS.name().equals(appProperties.getCloudProvider())) {
            return new ManageDataStoreImpl(appProperties.getAwsCredentialProfileName());
        } else {
            throw new IllegalArgumentException("Invalid cloud provider provided");
        }
    }
}
