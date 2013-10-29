package org.wso2.carbon.cep.wihidum.core.bucket;

import org.wso2.carbon.cep.admin.internal.CEPAdminRemoteBucketDeployer;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.distributing.DistributingBucketProvider;
import java.util.Map;
import org.apache.log4j.Logger;
import org.wso2.carbon.cep.core.distributing.DistributingWihidumValueHolder;


public class RemoteBucketDeployManager implements DistributingWihidumValueHolder {

   private static Logger logger = Logger.getLogger(RemoteBucketDeployManager.class);
   private DistributingBucketProvider distributingBucketProvider = DistributingBucketProvider.getInstance();
   private BucketSplitter bucketSplitter;
    private CEPAdminRemoteBucketDeployer remoteBucketDeployer;


    public RemoteBucketDeployManager(){
         bucketSplitter = new BucketSplitter();
        remoteBucketDeployer = CEPAdminRemoteBucketDeployer.getInstance();

     }

    @Override
    public void execute(){
        logger.info("Run the execute method in  Remote Bucket Deploy Manager");
          if(distributingBucketProvider.isUpdate()){
              logger.info("Distributing bucket Provider updated");
              synchronized (distributingBucketProvider){
                Bucket bucket = distributingBucketProvider.getBucket();
                Map<String,Bucket> map =  bucketSplitter.getBucketList(bucket);
                  logger.info("Map Size" +map.size());
                 for(String key : map.keySet()){
                     try {
                         remoteBucketDeployer.deploy(key,map.get(key));
                         logger.info("run deploy in affter spilitting buckets  bucket is " +map.get(key).getName());
                     } catch (Exception e){
                         logger.info(e.getMessage());
                     }
                 }
              }
              distributingBucketProvider.setUpdate(false);
          }
    }
}
