package org.wso2.carbon.cep.wihidum.core.bucket.splitter;

import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Expression;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.input.mapping.InputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.TupleInputMapping;
import org.wso2.carbon.cep.core.mapping.input.property.TupleInputProperty;
import org.wso2.carbon.cep.core.mapping.output.Output;
import org.wso2.carbon.cep.core.mapping.output.mapping.OutputMapping;
import org.wso2.carbon.cep.core.mapping.output.mapping.TupleOutputMapping;
import org.wso2.carbon.cep.core.mapping.output.property.TupleOutputProperty;
import org.wso2.carbon.cep.wihidum.core.broker.RemoteBrokerDeployer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hp
 * Date: 10/7/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class PatternSplitter {

    private static final String DISTRIBUTED_PROCESSING = "siddhi.enable.distributed.processing";


    //from e1 = cseEventStream [ price >= 50 and volume > 100 ] -> e2 = cseEventStream [price <= 40 ] <:5>
    // -> e3 = cseEventStream [volume <= 70 ] insert into StockQuote e1.symbol as symbol1,e2[0].symbol as symbol2,e3.symbol as symbol3 ;

    //from every (a1 = infoStock[action == "buy"] -> a2 = confirmOrder[command == "OK"] )
    //     -> b1 = StockExchangeStream [price > infoStock.price]
    //within 3000
    //insert into StockQuote
    //a1.action as action, b1.price as price

    public Map<String, Bucket> getBucketList(Bucket bucket) {
        return splitPatternQuery(bucket, bucket.getQueries().get(0));

    }


    private Map<String, Bucket> splitPatternQuery(Bucket bucket, Query patternQuery) {
        Map<String, Bucket> bucketMap = new HashMap<String, Bucket>();

        List<String> ipList = patternQuery.getIpList();
        String queryText = patternQuery.getExpression().getText();
        String[] conditions = queryText.split("->");
        int i;
        for (i = 0; i < conditions.length; i++) {


            Query subQuery = new Query();
            String expression;
            String inputStreamWithCondition = conditions[i].substring(conditions[i].indexOf("=") + 1, conditions[i].indexOf("]") + 1);
            if (inputStreamWithCondition.contains(".")) {
                break;
            }

            String[] streamNameSet = inputStreamWithCondition.split("\\s+");
            String inputStream;
            if (streamNameSet[0].matches("[a-z]"))
                inputStream = streamNameSet[0];
            else
                inputStream = streamNameSet[1];


            List<Input> inputList = bucket.getInputs();
            Input subQueryInput = null;
            for (Input input : inputList) {
                if (inputStream.equalsIgnoreCase(input.getInputMapping().getStream())) {
                    subQueryInput = input;
                    break;
                }
            }
            expression = "from " + inputStreamWithCondition + "insert into " + inputStream + "P ";

            Bucket subBucket = new Bucket();
            subBucket.setDescription(bucket.getDescription());
            subBucket.setEngineProvider(bucket.getEngineProvider());

            subBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());
            if (subBucket.getProviderConfigurationProperties() != null) {
                subBucket.getProviderConfigurationProperties().setProperty(DISTRIBUTED_PROCESSING, "false");
            }
            subBucket.setMaster(false);
            List<Input> subBucketInput = new ArrayList<Input>();
            subBucketInput.add(subQueryInput);
            subBucket.setInputs(subBucketInput);


            subBucket.setName(patternQuery.getName() + i);


            subQuery.setName(patternQuery.getName());

            Output subQueryOutput = new Output();
            subQueryOutput.setBrokerName(ipList.get(ipList.size() - 1));

            TupleOutputMapping tupleOutputMapping = new TupleOutputMapping();

            List<TupleOutputProperty> outputProperties = new ArrayList<TupleOutputProperty>();


            TupleInputMapping tupleInputMapping = (TupleInputMapping) subQueryInput.getInputMapping();

            for (TupleInputProperty inputProperty : tupleInputMapping.getProperties()) {
                expression +=  inputProperty.getName() + " , ";
                outputProperties.add(new TupleOutputProperty(inputProperty.getName(), inputProperty.getName(), inputProperty.getType()));
            }


            tupleOutputMapping.setPayloadDataProperties(outputProperties);

            subQueryOutput.setOutputMapping(tupleOutputMapping);
            subQueryOutput.setTopic(subQueryInput.getTopic() + "1");

            subQuery.setOutput(subQueryOutput);

            Expression queryExpression = new Expression();
            expression = expression.substring(0,expression.length()-2) + ";";
            queryExpression.setText(expression);
            subQuery.setExpression(queryExpression);


            List<Query> queryList = new ArrayList<Query>();
            queryList.add(subQuery);
            subBucket.setQueries(queryList);


            bucketMap.put(patternQuery.getIpList().get(i), subBucket);


        }


        //complete pattern query
        Bucket patternBucket = new Bucket();
        patternBucket.setDescription(bucket.getDescription());
        patternBucket.setEngineProvider(bucket.getEngineProvider());

        //List<Input> patternBucketInput = new ArrayList<Input>();
        for (Input input : bucket.getInputs()) {
            Input patternInput = new Input();
            patternInput.setBrokerName("localAgentBroker");
            patternInput.setSubscriptionId(input.getSubscriptionId());
            patternInput.setTopic(input.getTopic()+"1");

            TupleInputMapping inputMapping = new TupleInputMapping();
            inputMapping.setStream(input.getInputMapping().getStream() + "P");
            inputMapping.setProperties(((TupleInputMapping) input.getInputMapping()).getProperties());
            ;
            patternInput.setInputMapping(inputMapping);

            patternBucket.addInput(patternInput);
        }
        //patternBucket.setInputs();
        patternBucket.setName(patternQuery.getName());

        patternBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());
        if (patternBucket.getProviderConfigurationProperties() != null) {
            patternBucket.getProviderConfigurationProperties().setProperty(DISTRIBUTED_PROCESSING, "false");
        }

        Query finalQuery = new Query();

        Expression finalQueryExpression = new Expression();

        for (Input input : bucket.getInputs()) {
            queryText = queryText.replace(input.getInputMapping().getStream(), input.getInputMapping().getStream() + "P");
        }
        finalQueryExpression.setText(queryText);


        finalQuery.setExpression(finalQueryExpression);
        finalQuery.setName(patternQuery.getName());
        Output output = patternQuery.getOutput();
       // output.setBrokerName("externalAgentBroker");
        finalQuery.setOutput(output);
        patternBucket.addQuery(finalQuery);

        RemoteBrokerDeployer remoteBucketDeployer = RemoteBrokerDeployer.getInstance();
        remoteBucketDeployer.deploy(output.getBrokerName(),ipList.get(ipList.size() - 1));

        bucketMap.put(ipList.get(ipList.size() - 1), patternBucket);
        return bucketMap;
    }

}
