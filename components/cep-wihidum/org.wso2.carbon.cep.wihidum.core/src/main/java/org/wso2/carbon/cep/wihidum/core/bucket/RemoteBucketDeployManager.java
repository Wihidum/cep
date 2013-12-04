package org.wso2.carbon.cep.wihidum.core.bucket;

import org.apache.axiom.om.OMElement;
import org.wso2.carbon.cep.admin.internal.CEPAdminRemoteBucketDeployer;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.distributing.DistributingBucketProvider;
import java.util.Map;
import org.apache.log4j.Logger;
import org.wso2.carbon.cep.core.distributing.DistributingWihidumValueHolder;
import org.wso2.carbon.cep.core.distributing.Util;
import org.wso2.carbon.cep.wihidum.core.cluster.ClusterManager;
import org.wso2.carbon.cep.wihidum.core.cluster.Constants;
import org.wso2.carbon.cep.wihidum.core.cluster.NodeNominator;


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
                  setClusterConfigs(bucket);
                Map<String,Bucket> map =  bucketSplitter.getBucketList(bucket);
                  logger.info("Map Size" +map.size());
                 for(String key : map.keySet()){
                     try {
                         remoteBucketDeployer.deploy(key,map.get(key));
                         logger.info("run deploy in after splitting buckets  bucket is " +map.get(key).getName());
                     } catch (Exception e){
                         logger.info(e.getMessage());
                     }
                 }
              }
              distributingBucketProvider.setUpdate(false);
          }
    }

    private void setClusterConfigs(Bucket bucket) {
        ClusterManager clusterManager = ClusterManager.getInstant();
        String manger = clusterManager.getLocalMemberAddress();
        clusterManager.setClusterConfigurations(Constants.MANAGER, manger);
        OMElement bucketOM = Util.getOM(bucket);
        clusterManager.setClusterConfigurations(Constants.MASTER_BUCKET, bucketOM);
        NodeNominator nodeNominator = new NodeNominator();
        String deputyManager = nodeNominator.nominateDeputyManager();
        clusterManager.setClusterConfigurations(Constants.DEPUTY_MANAGER, deputyManager);


    }
}
