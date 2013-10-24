package org.wso2.carbon.cep.wihidum.core.bucket;


import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.distributing.loadbalancer.Loadbalancer;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.output.Output;
import org.wso2.carbon.cep.wihidum.core.broker.BrokerConfiguration;
import org.wso2.carbon.cep.wihidum.core.broker.BrokerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BucketSplitter {

    public Map<String, Bucket> getBucketList(Bucket bucket){
        Map<String, BrokerConfiguration> brokerMap = BrokerProvider.getBrokers();
        Map<String, Bucket> bucketMap = new HashMap<String, Bucket>();
        List<Query> queryList = bucket.getQueries();
        List<Loadbalancer> loadbalancerList = bucket.getLoadbalancerList();
        for (Query query : queryList){
            List<String> ipList = query.getIpList();
            if (!ipList.isEmpty()){
                for (String ip : ipList) {
                    BrokerConfiguration broker = brokerMap.get(ip);
                    if(broker !=null){
                    Bucket smallBucket = createBucket(bucket, query, broker);
                    bucketMap.put(ip, smallBucket);
                    }
                }
            }
        }
            if(loadbalancerList.size()>0){
                for(Loadbalancer loadbalancer:loadbalancerList){
                    Bucket bucketlb =  createBucketForLB(loadbalancer);
                    bucketMap.put(loadbalancer.getIp(),bucketlb);
                }
            }


        return bucketMap;
    }


    private Bucket createBucket(Bucket bucket, Query query, BrokerConfiguration  broker){
        Output output = query.getOutput();
       // output.setBrokerName(broker.getOutputBroker());
      //  output.setTopic(broker.getOutputTopic());
        List<Input> inputList = bucket.getInputs();
        if (!inputList.isEmpty()){
            List<Input> newInputList = new ArrayList<Input>();
            for (Input input : inputList) {
                input.setBrokerName(broker.getInputBroker());
               // input.setTopic(broker.getInputTopic());
                newInputList.add(input);
            }
        }
        Query newQuery = new Query();
        newQuery.setExpression(query.getExpression());
        newQuery.setName(query.getName());
        newQuery.setExpression(query.getExpression());
        newQuery.setOutput(output);
        Bucket newBucket = new Bucket();
        newBucket.setDescription(bucket.getDescription());
        newBucket.setMaster(false);
        newBucket.setEngineProvider(bucket.getEngineProvider());
        newBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());
        newBucket.setName(broker.getIp());
        newBucket.setInputs(inputList);
        newBucket.addQuery(newQuery);
        return newBucket;
    }



    private Bucket createBucketForLB(Loadbalancer loadbalancer){

        Bucket bucket = new Bucket();
        bucket.addLoadbalancerNode(loadbalancer);
        bucket.setName("loadbalancer");
        return bucket;
    }


}
