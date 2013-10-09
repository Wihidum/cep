package org.wso2.carbon.cep.wihidum.core.bucket;


import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.wihidum.core.bucket.splitter.QuerySplitter;

import java.util.Map;

public class BucketSplitter {

    public Map<String, Bucket> getBucketList(Bucket bucket){
        return new QuerySplitter().getBucketList(bucket);
    }


//    private Bucket createBucket(Bucket bucket, Query query, BrokerConfiguration  broker){
//        Output output = query.getOutput();
//        output.setBrokerName("externalAgentBroker");
//      //  output.setTopic(broker.getOutputTopic());
//        List<Input> inputList = bucket.getInputs();
//        if (!inputList.isEmpty()){
//            List<Input> newInputList = new ArrayList<Input>();
//            for (Input input : inputList) {
//                input.setBrokerName(broker.getInputBroker());
//               // input.setTopic(broker.getInputTopic());
//                newInputList.add(input);
//            }
//        }
//        Query newQuery = new Query();
//        newQuery.setExpression(query.getExpression());
//        newQuery.setName(query.getName());
//        newQuery.setExpression(query.getExpression());
//        newQuery.setOutput(output);
//        Bucket newBucket = new Bucket();
//        newBucket.setDescription(bucket.getDescription());
//        newBucket.setMaster(false);
//        newBucket.setEngineProvider(bucket.getEngineProvider());
//        newBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());
//        newBucket.getProviderConfigurationProperties().setProperty("siddhi.enable.distributed.processing","false");
//        newBucket.setName(broker.getIp());
//        newBucket.setInputs(inputList);
//        newBucket.addQuery(newQuery);
//        return newBucket;
//    }












}
