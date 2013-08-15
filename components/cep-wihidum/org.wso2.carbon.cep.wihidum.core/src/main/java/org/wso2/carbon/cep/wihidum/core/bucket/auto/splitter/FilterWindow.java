package org.wso2.carbon.cep.wihidum.core.bucket.auto.splitter;

import com.hazelcast.core.HazelcastInstance;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.output.Output;
import org.wso2.carbon.cep.wihidum.core.broker.BrokerConfiguration;
import org.wso2.carbon.cep.wihidum.core.broker.BrokerProvider;
import org.wso2.carbon.cep.wihidum.core.cluster.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sachini
 * Date: 8/15/13
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilterWindow {

    private static final String DISTRIBUTED_PROCESSING= "siddhi.enable.distributed.processing";
    public void splitBucket(Bucket bucket){
        ClusterManager clusterManager =  ClusterManager.getInstant();
        clusterManager.getMemberList();

    }

    public void getBucketLists(Bucket bucket){
        Map<String, Object> streamDefinitionMap =  new HashMap<String, Object>();

        List<Query> queryList = bucket.getQueries();
        for (Query query : queryList){
            List<String> ipList = query.getIpList();
            if (!ipList.isEmpty()){
                for (String ip : ipList) {
                      createBucket(bucket,query,ip,streamDefinitionMap);


                }
            }
        }
    }

    private void createBucket(Bucket bucket, Query query, String ip, Map<String,Object> streamDefinitionMap) {
        Bucket subBucket = new Bucket();
        subBucket.setMaster(false);
        subBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());
        subBucket.getProviderConfigurationProperties().setProperty(DISTRIBUTED_PROCESSING, "false");
        subBucket.setName(query.getName());
        subBucket.setDescription("sub bucket -" + bucket.getDescription());
        subBucket.setEngineProvider(bucket.getEngineProvider());

        List<Input> inputList = bucket.getInputs();
        if (!inputList.isEmpty() && streamDefinitionMap.isEmpty()){
           for (Input input : inputList) {
                   streamDefinitionMap.put(input.getInputMapping().getStream(),input);
            }
        }

        Query subQuery = new Query();
        subQuery.setExpression(query.getExpression());
        subQuery.setName(query.getName());

        Output output = query.getOutput();
        Output subQueryOutput = new Output();
        subQueryOutput.setOutputMapping(output.getOutputMapping());
        subQueryOutput.setTopic(output.getTopic());
        subQueryOutput.setBrokerName("externalAgentBroker");

        subQuery.setOutput(subQueryOutput);
        /////////////////////////////////////////////////////////
        //add output to map
        ////////////////////////////////////
        List<Query> subQueryList = new ArrayList<Query>();
        subQueryList.add(subQuery);
        subBucket.setQueries(subQueryList);

        List<Input> subQueryInput = new ArrayList<Input>();
        ////////////////////////////////////////
        //query input
        ////////////////////////////////////////
        subBucket.setInputs(subQueryInput);






    }



    public Map<String, Bucket> getBucketList(Bucket bucket){
        Map<String, BrokerConfiguration> brokerMap = BrokerProvider.getBrokers();
        Map<String, Bucket> bucketMap = new HashMap<String, Bucket>();
        List<Query> queryList = bucket.getQueries();
        for (Query query : queryList){
            List<String> ipList = query.getIpList();
            if (!ipList.isEmpty()){
                for (String ip : ipList) {
                         Bucket smallBucket = createBucket(bucket, query);
                        bucketMap.put(ip, smallBucket);

                }
            }
        }
        return bucketMap;
    }


    private Bucket createBucket(Bucket bucket, Query query){
        Bucket subBucket = new Bucket();
        List<Input> inputList = bucket.getInputs();

        Output output = query.getOutput();
        output.setBrokerName("externalAgentBroker");
        //  output.setTopic(broker.getOutputTopic());
        //List<Input> inputList = bucket.getInputs();
        if (!inputList.isEmpty()){
            List<Input> newInputList = new ArrayList<Input>();
            for (Input input : inputList) {
                //input.setBrokerName(broker.getInputBroker());
                // input.setTopic(broker.getInputTopic());
                newInputList.add(input);
                input.getInputMapping().
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
        newBucket.getProviderConfigurationProperties().setProperty("siddhi.enable.distributed.processing","false");
      //  newBucket.setName(broker.getIp());
        newBucket.setInputs(inputList);
        newBucket.addQuery(newQuery);
        return newBucket;
    }


}
