package org.wso2.carbon.cep.wihidum.core.cluster;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cep.admin.internal.CEPAdminRemoteBucketDeployer;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.exception.CEPConfigurationException;
import org.wso2.carbon.cep.wihidum.core.internal.WihidumCoreValueHolder;

import java.util.ArrayList;
import java.util.List;

public class FaultHandler {
    private static FaultHandler faultHandler;
    private String localIP;
    private static final Log log = LogFactory.getLog(FaultHandler.class);
    private ClusterManager clusterManager;
    private NodeNominator nominator;

    private FaultHandler(){
        this.localIP = ClusterManager.getInstant().getLocalMemberAddress();
        this.clusterManager = ClusterManager.getInstant();
    }

    public static FaultHandler getInstance(){
        if(faultHandler == null){
            faultHandler = new FaultHandler();
        }
        return faultHandler;
    }

    public void handle(String ipAddress){
        try {
            WihidumCoreValueHolder.getInstance().getCEPService().removeAllBuckets();
            String manager = (String)ClusterManager.getInstant().getBucketConfigurations().get(Constants.MANAGER);
            String deputyManager = (String)ClusterManager.getInstant().getBucketConfigurations().get(Constants.DEPUTY_MANAGER);
            nominator = new NodeNominator();
            Bucket bucket;
            if(manager.equals(localIP)){
               if(ipAddress.equals(deputyManager)){
                  clusterManager.setClusterConfigurations(Constants.DEPUTY_MANAGER, nominator.nominateDeputyManager());
               }
                bucket = (Bucket)clusterManager.getBucketConfigurations().get(Constants.MASTER_BUCKET);
                reconfigureBucket(bucket,ipAddress);
            }
            else if(ipAddress.equals(manager) && deputyManager.equals(localIP)){

                bucket = (Bucket)clusterManager.getBucketConfigurations().get(Constants.MASTER_BUCKET);
                reconfigureBucket(bucket,ipAddress);
            }
        } catch (CEPConfigurationException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    private void reconfigureBucket(Bucket bucket, String ipAddress) {
        List<Query> queryList  = bucket.getQueries();
        ArrayList<String> ipList;
        int index = 0;
        for(Query query:queryList){

           ipList = (ArrayList<String>) query.getIpList();
           if(ipList.contains(ipAddress)){
              if (ipList.size()>1){   //if more than one node present simply ignore node
                  ipList.remove(ipAddress);
                  bucket.getQueries().get(index).clearIPList();
                  for (String ip:ipList){
                  bucket.getQueries().get(index).addIP(ip);
                  }
              }else{                    //if only this node nominate another node and use that node
                  String nominatedIp = nominator.nominateCEPNode();
                  bucket.getQueries().get(index).clearIPList();
                  bucket.getQueries().get(index).addIP(nominatedIp);
              }

           }
           index ++;
        }
        clusterManager.setClusterConfigurations(Constants.MASTER_BUCKET, bucket);
        deployBucket(bucket);
    }

    private void deployBucket(Bucket bucket) {
        try {
            CEPAdminRemoteBucketDeployer.getInstance().deploy(localIP, bucket);
        } catch (Exception e) {
            log.error("Error when adding bucket "+bucket.getName(),e);
        }
    }
}

