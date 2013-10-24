package org.wso2.carbon.cep.wihidum.core.cluster;


public class NodeNominator {
    private ClusterManager clusterManager;

    public NodeNominator(){
       this.clusterManager = ClusterManager.getInstant();
    }

    public String nominateDeputyManager(){
       return clusterManager.getMemberList().get(1);
    }

    public String nominateCEPNode() {
        return clusterManager.getMemberList().get(1);
    }
}
