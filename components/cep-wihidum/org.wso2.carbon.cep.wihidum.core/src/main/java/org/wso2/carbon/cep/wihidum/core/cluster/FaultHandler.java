package org.wso2.carbon.cep.wihidum.core.cluster;


import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cep.admin.internal.CEPAdminRemoteBucketDeployer;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.distributing.loadbalancer.InnerOutputNode;
import org.wso2.carbon.cep.core.distributing.loadbalancer.LBOutputNode;
import org.wso2.carbon.cep.core.distributing.loadbalancer.Loadbalancer;
import org.wso2.carbon.cep.core.distributing.loadbalancer.Stream;
import org.wso2.carbon.cep.core.exception.CEPConfigurationException;
import org.wso2.carbon.cep.core.internal.config.BucketHelper;
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
                clusterManager.setClusterConfigurations(Constants.MANAGER, localIP);
                clusterManager.setClusterConfigurations(Constants.DEPUTY_MANAGER, nominator.nominateDeputyManager());
                OMElement bucketOM = (OMElement)(clusterManager.getClusterConfigurations(Constants.MASTER_BUCKET));
                bucket = BucketHelper.fromOM(bucketOM);
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
              //if (ipList.size()>1){   //if more than one node present simply ignore node
                  ipList.remove(ipAddress);
                  bucket.getQueries().get(index).clearIPList();
                  String nominatedIp = nominator.nominateCEPNode();
                  for (String ip:ipList){
                  bucket.getQueries().get(index).addIP(ip);
                  }
                  bucket.getQueries().get(index).addIP(nominatedIp);
                  bucket = reconfigureLB(bucket,ipAddress,null,false);
             /* }else{                    //if only this node nominate another node and use that node
                  String nominatedIp = nominator.nominateCEPNode();
                  bucket.getQueries().get(index).clearIPList();
                  bucket.getQueries().get(index).addIP(nominatedIp);
              }*/

           }
           index ++;
        }
        clusterManager.setClusterConfigurations(Constants.MASTER_BUCKET, bucket);
        deployBucket(bucket);
    }

    private Bucket reconfigureLB(Bucket bucket, String ipAddress, String alternateIp, boolean disposable) {
        List<Loadbalancer> loadbalancerList = bucket.getLoadbalancerList();
        for(Loadbalancer lb:loadbalancerList){
            //check for usage of outputnodelist
            List<LBOutputNode> outputNodeList = lb.getOutputNodeList();
            for(int i=0; i<outputNodeList.size();i++){
                if(outputNodeList.get(i).getIp().equals(ipAddress)){
                   outputNodeList.remove(i);
                    if(!disposable){
                        LBOutputNode alternate = new LBOutputNode();
                        alternate.setIp(alternateIp);
                        alternate.setPort("7611");
                        outputNodeList.add(alternate);
                    }
                }
            }
            //List<InnerOutputNode> innerOutputNodeList;
            if(lb.getType().equals("esd")){
               for(int i=0; i<lb.getStreamList().size();i++){
                   List<InnerOutputNode> innerOutputNodeList= lb.getStreamList().get(i).getInnerOutputNodeList();
                   innerOutputNodeList = reconfigureOutputNodes(innerOutputNodeList,ipAddress,alternateIp,disposable);
                   lb.getStreamList().get(i).setInnerOutputNodeList(innerOutputNodeList);
               }
            }else  if(lb.getType().equals("rrd")){
                List<InnerOutputNode> innerOutputNodeList=lb.getInnerOutputNodeList();
                innerOutputNodeList = reconfigureOutputNodes(innerOutputNodeList,ipAddress,alternateIp,disposable);
                lb.setInnerOutputNodeList(innerOutputNodeList);
            }


        }
        return bucket;
    }

    private List<InnerOutputNode> reconfigureOutputNodes(List<InnerOutputNode> innerOutputNodeList, String ipAddress, String alternateIp, Boolean disposable) {
        for(int j=0; j<innerOutputNodeList.size();j++){
        for(int k=0; k<innerOutputNodeList.get(j).getLbOutputNodeList().size();k++){
            if(innerOutputNodeList.get(j).getLbOutputNodeList().get(k).equals(ipAddress)){
                innerOutputNodeList.get(j).getLbOutputNodeList().remove(k);
                if(!disposable){
                    LBOutputNode alternate = new LBOutputNode();
                    alternate.setIp(alternateIp);
                    alternate.setPort("7611");
                    innerOutputNodeList.get(j).getLbOutputNodeList().add(alternate);
                }
            }
        }
        }
        return innerOutputNodeList;
    }

    private void deployBucket(Bucket bucket) {
        try {
            CEPAdminRemoteBucketDeployer.getInstance().deploy(localIP, bucket);
        } catch (Exception e) {
            log.error("Error when adding bucket "+bucket.getName(),e);
        }
    }
}

