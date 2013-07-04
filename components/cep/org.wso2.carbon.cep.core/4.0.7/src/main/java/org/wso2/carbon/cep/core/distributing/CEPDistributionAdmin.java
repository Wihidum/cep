package org.wso2.carbon.cep.core.distributing;

import org.wso2.carbon.cep.core.Bucket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CEPDistributionAdmin {

    public static CEPDistributionAdmin cepDistributionAdmin = null;

    public abstract Map<String, Bucket> getBucketList(Bucket bucket, String bucketpath);


    public static CEPDistributionAdmin getInstances() {
        cepDistributionAdmin = DistributingBucketProvider.getInstance();

        return cepDistributionAdmin;
    }


}
