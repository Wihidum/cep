package org.wso2.carbon.cep.wihidum.core.broker;


import org.wso2.carbon.cep.core.distributing.util.BrokerConfig;
import org.wso2.carbon.cep.wihidum.core.cluster.ClusterManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokerProvider {


    public static Map<String, BrokerConfig> getBrokers() {
         ClusterManager clusterManager = ClusterManager.getInstant();
         List<String> brokerNamesList = clusterManager.getMemberList();
         Map<String,BrokerConfig> brokerConfigMap = new HashMap<String, BrokerConfig>();
         for(String ip:brokerNamesList){
             BrokerConfig brokerConfig = new BrokerConfig(ip,ip,null,null,null);
             brokerConfigMap.put(ip,brokerConfig);
         }
        return brokerConfigMap;
    }
}
