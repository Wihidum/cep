package org.wso2.carbon.cep.wihidum.core.cluster;


import org.wso2.carbon.cep.core.distributing.loadbalancer.Loadbalancer;

import java.util.List;

public class NodeNominator {
    private ClusterManager clusterManager;

    public NodeNominator(){
       this.clusterManager = ClusterManager.getInstant();
    }

    public String nominateDeputyManager(){
       if((clusterManager.getClusterConfigurations(Constants.LOADBALANCER_LIST))!= null){
           List<Loadbalancer> lbList = (List<Loadbalancer>) clusterManager.getClusterConfigurations(Constants.LOADBALANCER_LIST);
           List<String> memeberList = clusterManager.getMemberList();
           for (Loadbalancer lb:lbList){
               memeberList.remove(lb.getIp());
           }
           return memeberList.get(1);
       }
       return clusterManager.getMemberList().get(1);
    }

    public String nominateCEPNode() {
        return clusterManager.getMemberList().get(1);
    }
}
