package org.wso2.carbon.cep.wihidum.core.cluster;


import org.apache.axiom.om.OMElement;
import org.wso2.carbon.cep.core.distributing.Util;
import org.wso2.carbon.cep.core.distributing.loadbalancer.Loadbalancer;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.exception.CEPConfigurationException;
import org.wso2.carbon.cep.core.internal.config.BucketHelper;

import java.util.List;

public class NodeNominator {
    private ClusterManager clusterManager;

    public NodeNominator(){
       this.clusterManager = ClusterManager.getInstant();
    }

    public String nominateDeputyManager(){
       Object bucket = clusterManager.getClusterConfigurations(Constants.MASTER_BUCKET);

       if(bucket != null){
           try {
               Bucket bucket1 = Util.getBucket((OMElement) bucket);
               if((bucket1).getLoadbalancerList()!= null){
                OMElement om =   (OMElement)(clusterManager.getClusterConfigurations(Constants.MASTER_BUCKET));
                   Bucket bucket2 = Util.getBucket(om);
                   List<Loadbalancer> lbList = (List<Loadbalancer>)  bucket2.getLoadbalancerList();
                   List<String> memberList = clusterManager.getMemberList();
                   for (Loadbalancer lb:lbList){
                       memberList.remove(lb.getIp());
                   }
                   memberList.remove(clusterManager.getClusterConfigurations(Constants.MANAGER));
                   return memberList.get(1);
               }
           } catch (CEPConfigurationException e) {
               e.printStackTrace();
           }

       }
       return clusterManager.getMemberList().get(1);
    }

    public String nominateCEPNode() {
        return clusterManager.getMemberList().get(1);
    }
}
