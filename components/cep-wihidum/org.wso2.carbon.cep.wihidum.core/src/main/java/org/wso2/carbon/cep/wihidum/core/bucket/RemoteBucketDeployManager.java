package org.wso2.carbon.cep.wihidum.core.bucket;

import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.RemoteBucketDeployer;
import org.wso2.carbon.cep.core.distributing.DistributingBucketProvider;
import java.util.Map;
import org.apache.log4j.Logger;
import org.wso2.carbon.cep.core.distributing.DistributingWihidumValueHolder;
import org.wso2.carbon.cep.core.distributing.WihidumValueHolder;

public class RemoteBucketDeployManager implements DistributingWihidumValueHolder {

   private static Logger logger = Logger.getLogger(RemoteBucketDeployManager.class);
   private DistributingBucketProvider distributingBucketProvider = DistributingBucketProvider.getInstance();
   private BucketSplitter bucketSplitter;



    public RemoteBucketDeployManager(){
         bucketSplitter = new BucketSplitter();

     }

    @Override
    public void execute(){
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
