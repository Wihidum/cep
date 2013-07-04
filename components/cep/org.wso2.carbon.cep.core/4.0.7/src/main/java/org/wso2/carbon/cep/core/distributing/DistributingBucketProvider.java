package org.wso2.carbon.cep.core.distributing;


import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.distributing.util.BrokerConfig;
import org.wso2.carbon.cep.core.distributing.util.BrokerProvider;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.output.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributingBucketProvider extends CEPDistributionAdmin {

    private static DistributingBucketProvider distributingBucketProvider = null;

    private DistributingBucketProvider() {

    }

    public Map<String, Bucket> getBucketList(Bucket bucket, String bucketpath) {
        Map<String, BrokerConfig> brokerMap = BrokerProvider.getBrokers();
        Map<String, Bucket> bucketMap = new HashMap<String, Bucket>();
        List<Query> queryList = bucket.getQueries();
        for (Query query : queryList) {
            List<String> ipList = query.getIpList();
            if (!ipList.isEmpty()) {
                for (String ip : ipList) {
                    BrokerConfig broker = brokerMap.get(ip);
                    Bucket smallBucket = createBucket(bucket, query, broker);
                    bucketMap.put(ip, smallBucket);
                }
            }
        }
        return null;
    }


    public static DistributingBucketProvider getInstance() {
        if (distributingBucketProvider == null) {
            distributingBucketProvider = new DistributingBucketProvider();
        }
        return distributingBucketProvider;
    }

    private Bucket createBucket(Bucket bucket, Query query, BrokerConfig broker) {

        Output output = query.getOutput();
        output.setBrokerName(broker.getOutputBroker());
        output.setTopic(broker.getOutputTopic());
        List<Input> inputList = bucket.getInputs();
        if (!inputList.isEmpty()) {
            List<Input> newInputList = new ArrayList<Input>();
            for (Input input : inputList) {
                input.setBrokerName(broker.getInputBroker());
                input.setTopic(broker.getInputTopic());
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
        newBucket.setEngineProvider(bucket.getEngineProvider());
        newBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());
        newBucket.setName(broker.getIp());
        newBucket.setInputs(inputList);
        newBucket.addQuery(newQuery);
        return newBucket;
    }


}
