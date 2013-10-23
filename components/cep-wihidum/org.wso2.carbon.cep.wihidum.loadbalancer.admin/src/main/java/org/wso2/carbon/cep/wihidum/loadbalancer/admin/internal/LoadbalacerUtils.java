package org.wso2.carbon.cep.wihidum.loadbalancer.admin.internal;


import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;

public class LoadbalacerUtils {


 public static LoadBalancerConfiguration adaptLoadbalancerConfiguration(LoadbalancerDTO loadbalancerDTO){
     LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
     NodeDTO[] nodeDTOs = loadbalancerDTO.getNodeList();
     loadBalancerConfiguration.clearOutputNodes();
     for(int i=0;i<nodeDTOs.length;i++){
     loadBalancerConfiguration.addOutputNode(nodeDTOs[i].getHostname(),nodeDTOs[i].getPort());
     }
    return  loadBalancerConfiguration;
 }








}
