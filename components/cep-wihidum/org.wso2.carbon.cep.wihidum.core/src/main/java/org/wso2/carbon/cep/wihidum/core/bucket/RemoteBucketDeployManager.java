package org.wso2.carbon.cep.wihidum.core.bucket;

import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.RemoteBucketDeployer;
import org.wso2.carbon.cep.core.distributing.DistributingBucketProvider;
import java.util.Map;
import org.apache.log4j.Logger;

public class RemoteBucketDeployManager implements Runnable {

   private static Logger logger = Logger.getLogger(RemoteBucketDeployManager.class);
   private DistributingBucketProvider distributingBucketProvider = DistributingBucketProvider.getInstance();
   private BucketSplitter bucketSplitter;


    public RemoteBucketDeployManager(){
         bucketSplitter = new BucketSplitter();
     }

    @Override
    public void run() {
      while(true){
          if(distributingBucketProvider.isUpdate()){
              synchronized (distributingBucketProvider){
                Bucket bucket = distributingBucketProvider.getBucket();
                Map<String,Bucket> map =  bucketSplitter.getBucketList(bucket);
                 for(String key : map.keySet()){
                     try {
                         RemoteBucketDeployer.deploy(key,map.get(key));
                     } catch (Exception e){
                         logger.info(e.getMessage());
                     }
                 }
              }
              distributingBucketProvider.setUpdate(false);
          }
      }
    }
}
