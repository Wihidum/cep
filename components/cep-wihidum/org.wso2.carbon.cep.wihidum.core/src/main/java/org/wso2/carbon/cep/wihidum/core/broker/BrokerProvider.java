package org.wso2.carbon.cep.wihidum.core.broker;

import org.wso2.carbon.cep.wihidum.core.cluster.ClusterManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokerProvider {


    public static Map<String, BrokerConfiguration> getBrokers() {
         ClusterManager clusterManager = ClusterManager.getInstant();
         List<String> brokerNamesList = clusterManager.getMemberList();
         Map<String,BrokerConfiguration> brokerConfigMap = new HashMap<String, BrokerConfiguration>();
         for(String ip:brokerNamesList){
             BrokerConfiguration brokerConfig = new BrokerConfiguration(ip,ip,null,null,null);
             brokerConfigMap.put(ip,brokerConfig);
         }
        return brokerConfigMap;
    }
}
