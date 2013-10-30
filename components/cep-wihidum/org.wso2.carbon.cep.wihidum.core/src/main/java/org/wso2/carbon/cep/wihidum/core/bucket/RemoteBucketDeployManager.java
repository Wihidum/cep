package org.wso2.carbon.cep.wihidum.core.bucket;

import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.RemoteBucketDeployer;
import org.wso2.carbon.cep.core.distributing.DistributingBucketProvider;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.wso2.carbon.cep.core.distributing.DistributingWihidumValueHolder;
import org.wso2.carbon.cep.core.distributing.WihidumValueHolder;
import org.wso2.carbon.cep.wihidum.core.cluster.ClusterManager;
import org.wso2.carbon.cep.wihidum.core.cluster.Constants;
import org.wso2.carbon.cep.wihidum.core.cluster.NodeNominator;

public class RemoteBucketDeployManager implements DistributingWihidumValueHolder {

   private static Logger logger = Logger.getLogger(RemoteBucketDeployManager.class);
   private DistributingBucketProvider distributingBucketProvider = DistributingBucketProvider.getInstance();
   private BucketSplitter bucketSplitter;



    public RemoteBucketDeployManager(){
         bucketSplitter = new BucketSplitter();

     }

    @Override
    public void execute(){
        logger.info("Run the execute method in  Remote Bucket Deploy Manager");
          if(distributingBucketProvider.isUpdate()){
              logger.info("Distributing bucket Provider updated");
              synchronized (distributingBucketProvider){
                Bucket bucket = distributingBucketProvider.getBucket();
                  setClusterConfigs(bucket);
                Map<String,Bucket> map =  bucketSplitter.getBucketList(bucket);
                  logger.info("Map Size" +map.size());
                 for(String key : map.keySet()){
                     try {
                         RemoteBucketDeployer.deploy(key,map.get(key));
                         logger.info("run deploy in affter spilitting buckets  bucket is " +map.get(key).getName());
                     } catch (Exception e){
                         logger.info(e.getMessage());
                     }
                 }
              }
              distributingBucketProvider.setUpdate(false);
          }
    }

    private void setClusterConfigs(Bucket bucket) {
        NodeNominator nodeNominator = new NodeNominator();
        ClusterManager clusterManager = ClusterManager.getInstant();
        String manger = clusterManager.getLocalMemberAddress();
        String deputyManager = nodeNominator.nominateDeputyManager();
        //List<String> loadbalancerList = bucket.get

        clusterManager.setClusterConfigurations(Constants.MANAGER, manger);
        clusterManager.setClusterConfigurations(Constants.DEPUTY_MANAGER, deputyManager);
        clusterManager.setClusterConfigurations(Constants.MASTER_BUCKET, bucket);
    }
}
