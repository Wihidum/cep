package org.wso2.carbon.cep.core.distributing;


import org.wso2.carbon.cep.core.Bucket;

public class DistributingBucketProvider extends CEPDistributionAdmin {

    private static DistributingBucketProvider distributingBucketProvider = null;
    private boolean isUpdate;
    private  Bucket bucket;

    private DistributingBucketProvider() {

    }

    public Bucket getBucket() {
        return bucket;
    }

    public void addBucket(Bucket bucket){
        this.bucket = bucket;
    }

    public synchronized void setUpdate(boolean update){
        this.isUpdate = update;
    }
     public boolean isUpdate(){
         return isUpdate;
     }


    public static DistributingBucketProvider getInstance() {
        if (distributingBucketProvider == null) {
            distributingBucketProvider = new DistributingBucketProvider();
        }
        return distributingBucketProvider;
    }



}
