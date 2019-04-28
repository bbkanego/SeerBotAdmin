package com.seerlogics.botadmin.factory;

import com.seerlogics.botadmin.config.AppProperties;
import com.seerlogics.cloud.ManageInstance;
import com.seerlogics.cloud.ManageLoadBalancer;
import com.seerlogics.cloud.aws.ec2.ManageInstanceImpl;
import com.seerlogics.cloud.aws.elb.ManageLoadBalancerImpl;
import com.seerlogics.cloud.common.CloudConstant;

/**
 * Created by bkane on 4/26/19.
 */
public class ManageLoadBalancerFactory {
    public ManageLoadBalancer createLoadBalancerManager(AppProperties appProperties) {
        if (CloudConstant.CloudEnvironment.AWS.name().equals(appProperties.getCloudProvider())) {
            return new ManageLoadBalancerImpl(appProperties.getAwsCredentialProfileName());
        } else {
            throw new IllegalArgumentException("Invalid cloud provider provided");
        }
    }
}
