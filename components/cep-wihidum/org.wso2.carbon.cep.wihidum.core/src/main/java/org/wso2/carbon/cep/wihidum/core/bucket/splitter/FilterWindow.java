package org.wso2.carbon.cep.wihidum.core.bucket.splitter;

import com.hazelcast.core.HazelcastInstance;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.RemoteBucketDeployer;
import org.wso2.carbon.cep.core.internal.util.CEPConstants;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.input.mapping.InputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.MapInputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.TupleInputMapping;
import org.wso2.carbon.cep.core.mapping.input.property.MapInputProperty;
import org.wso2.carbon.cep.core.mapping.input.property.TupleInputProperty;
import org.wso2.carbon.cep.core.mapping.output.Output;
import org.wso2.carbon.cep.core.mapping.output.mapping.*;
import org.wso2.carbon.cep.core.mapping.output.property.MapOutputProperty;
import org.wso2.carbon.cep.core.mapping.output.property.TupleOutputProperty;
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

    public Map<String, Bucket> getBucketLists(Bucket bucket){
        Map<String, Object> streamDefinitionMap =  new HashMap<String, Object>();
        Map<String, Bucket> bucketMap = new HashMap<String, Bucket>();
        List<Query> queryList = bucket.getQueries();
        for (Query query : queryList){
            List<String> ipList = query.getIpList();
            if (!ipList.isEmpty()){
                for (String ip : ipList) {
                      Bucket subBucket =   createBucket(bucket, query, ip, streamDefinitionMap);
                      bucketMap.put(ip,subBucket);
                }
            }
        }

        return bucketMap;
    }

    private Bucket createBucket(Bucket bucket, Query query, String ip, Map<String,Object> streamDefinitionMap) {
        Bucket subBucket = new Bucket();
        subBucket.setMaster(false);
        subBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());

        if(subBucket.getProviderConfigurationProperties() !=  null)    {
            subBucket.getProviderConfigurationProperties().setProperty(DISTRIBUTED_PROCESSING, "false");
        }

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

         subQueryOutput.setBrokerName(getOutputBroker(bucket,query.getOutputStream()));

        subQuery.setOutput(subQueryOutput);
        streamDefinitionMap.put(subQuery.getOutputStream(),output);




        List<Query> subQueryList = new ArrayList<Query>();
        subQueryList.add(subQuery);
        subBucket.setQueries(subQueryList);

        List<Input> subQueryInputs = new ArrayList<Input>();
        List<String> queryInputStreams = query.getInputStreams();
        for(String inputStream : queryInputStreams){
            Object inputStreamInfo = streamDefinitionMap.get(inputStream);
            if(inputStreamInfo != null && inputStreamInfo instanceof Input) {
                Input input = (Input) inputStreamInfo;
                input.setBrokerName("localAgentBroker");
                subQueryInputs.add((Input) inputStreamInfo);
            }

            else if (inputStreamInfo != null && inputStreamInfo instanceof Output) {
                Output outputToInput = (Output) inputStreamInfo;
                Input subQueryInput = new Input();
                subQueryInput.setBrokerName("localAgentBroker");
                subQueryInput.setTopic(outputToInput.getTopic());
                subQueryInput.setInputMapping(adaptOutput(outputToInput,inputStream));

            }
        }


        subBucket.setInputs(subQueryInputs);

        return subBucket;




    }


     private String getOutputBroker(Bucket bucket ,String outputStream) {
        List<String> outputBrokerip =new ArrayList<String>();
        for(Query query : bucket.getQueries()){

            for(String inputStream : query.getInputStreams()) {
                if(outputStream.equalsIgnoreCase(inputStream)){
                    for(String ip :query.getIpList()) {
                        outputBrokerip.add(ip) ;
                    }
                }
               if(outputBrokerip.size()>0)
                   break;
            }

        }
        if(outputBrokerip.size()>0)
            return   outputBrokerip.get(0);
        else
            return "externalAgentBroker";
    }


    private InputMapping adaptOutput(Output outputToInput, String inputStream) {

        OutputMapping outputMapping = outputToInput.getOutputMapping();
        if(outputMapping instanceof TupleOutputMapping)   {
            TupleInputMapping tupleInputMapping = new TupleInputMapping();
            tupleInputMapping.setStream(inputStream);

            TupleOutputMapping tupleOutputMapping = (TupleOutputMapping) outputMapping;
            List<TupleOutputProperty> tupleMetaOutputProperties = tupleOutputMapping.getMetaDataProperties();

            for(TupleOutputProperty property : tupleMetaOutputProperties){
                TupleInputProperty tupleInputProperty = new TupleInputProperty();
                tupleInputProperty.setInputDataType(CEPConstants.CEP_CONF_TUPLE_DATA_TYPE_META);
                tupleInputProperty.setInputName(property.getName());
                tupleInputProperty.setType(property.getType());
                tupleInputMapping.addProperty(tupleInputProperty);
            }

            List<TupleOutputProperty> tupleCorrelationOutputProperties = tupleOutputMapping.getCorrelationDataProperties();

            for(TupleOutputProperty property : tupleCorrelationOutputProperties){
                TupleInputProperty tupleInputProperty = new TupleInputProperty();
                tupleInputProperty.setInputDataType(CEPConstants.CEP_CONF_TUPLE_DATA_TYPE_CORRELATION);
                tupleInputProperty.setInputName(property.getName());
                tupleInputProperty.setType(property.getType());
                tupleInputMapping.addProperty(tupleInputProperty);
            }

            List<TupleOutputProperty> tuplePayloadOutputProperties = tupleOutputMapping.getPayloadDataProperties();

            for(TupleOutputProperty property : tuplePayloadOutputProperties){
                TupleInputProperty tupleInputProperty = new TupleInputProperty();
                tupleInputProperty.setInputDataType(CEPConstants.CEP_CONF_TUPLE_DATA_TYPE_PAYLOAD);
                tupleInputProperty.setInputName(property.getName());
                tupleInputProperty.setType(property.getType());
                tupleInputMapping.addProperty(tupleInputProperty);
            }

            return tupleInputMapping;

        }

//        if(outputMapping instanceof MapOutputMapping){
//            MapInputMapping mapInputMapping = new MapInputMapping();
//            mapInputMapping.setStream(inputStream);
//
//            List<MapInputProperty> properties = new ArrayList<MapInputProperty>();
//
//            MapOutputMapping mapOutputMapping = (MapOutputMapping) outputMapping;
//            List<MapOutputProperty> mapOutputProperties = mapOutputMapping.getPropertyList();
//
//            for(MapOutputProperty property : mapOutputProperties){
//                MapInputProperty mapInputProperty = new MapInputProperty();
//                mapInputProperty.setInputName(property.getName());
//             ////////////////////////////////////////////////////////////
//               mapInputProperty.setType(property.getValueOf());
//             //    Type?
//             //////////////////////////////////////////////////
//                mapInputProperty.setName(property.getName());
//                mapInputMapping.addProperty(mapInputProperty);
//            }
//
//            return mapInputMapping;
//
//        }
//        if(outputMapping instanceof XMLOutputMapping)  ;


        //////////////////////// //////////////////////
       // if(outputMapping instanceof TextOutputMapping);
        /////////////////////////////////////////////////


        return null;
    }








}
