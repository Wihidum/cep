package org.wso2.carbon.cep.wihidum.core.cluster;


import org.wso2.carbon.cep.core.distributing.loadbalancer.Loadbalancer;
import org.wso2.carbon.cep.core.Bucket;

import java.util.List;

public class NodeNominator {
    private ClusterManager clusterManager;

    public NodeNominator(){
       this.clusterManager = ClusterManager.getInstant();
    }

    public String nominateDeputyManager(){
       if(((Bucket)(clusterManager.getClusterConfigurations(Constants.MASTER_BUCKET))).getLoadbalancerList()!= null){
           List<Loadbalancer> lbList = (List<Loadbalancer>) ((Bucket)(clusterManager.getClusterConfigurations(Constants.MASTER_BUCKET))).getLoadbalancerList();
           List<String> memberList = clusterManager.getMemberList();
           for (Loadbalancer lb:lbList){
               memberList.remove(lb.getIp());
           }
           memberList.remove(clusterManager.getClusterConfigurations(Constants.MANAGER));
           return memberList.get(1);
       }
       return clusterManager.getMemberList().get(1);
    }

    public String nominateCEPNode() {
        return clusterManager.getMemberList().get(1);
    }
}
