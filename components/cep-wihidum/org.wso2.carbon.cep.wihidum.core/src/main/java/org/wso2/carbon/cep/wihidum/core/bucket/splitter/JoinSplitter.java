package org.wso2.carbon.cep.wihidum.core.bucket.splitter;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.wihidum.core.broker.RemoteBrokerDeployer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hp
 * Date: 10/8/13
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinSplitter {

    private static Logger logger = Logger.getLogger(JoinSplitter.class);
    private static final String DISTRIBUTED_PROCESSING = "siddhi.enable.distributed.processing";


    public Map<String, Bucket> getBucketList(Bucket bucket) {
        logger.info("Distributing join operator");
        return splitJoinQuery(bucket);
    }


    private Map<String, Bucket> splitJoinQuery(Bucket bucket) {
        Map<String, Bucket> bucketMap = new HashMap<String, Bucket>();
        Bucket subBucket = new Bucket();
        subBucket.setDescription(bucket.getDescription());
        subBucket.setEngineProvider(bucket.getEngineProvider());
        subBucket.setInputs(bucket.getInputs());
        subBucket.setMaster(false);
        subBucket.setName("sub bucket" + bucket.getName());

        subBucket.setProviderConfigurationProperties(bucket.getProviderConfigurationProperties());
        if (subBucket.getProviderConfigurationProperties() != null) {
            subBucket.getProviderConfigurationProperties().setProperty(DISTRIBUTED_PROCESSING, "false");
        }

        Query originalQuery = bucket.getQueries().get(0);
        Query subQuery = new Query();

        subQuery.setExpression(originalQuery.getExpression());
        subQuery.setName(originalQuery.getName());
        subQuery.setOutput(originalQuery.getOutput());

        subBucket.addQuery(subQuery);
        RemoteBrokerDeployer remoteBucketDeployer = RemoteBrokerDeployer.getInstance();

        for (String ip : bucket.getQueries().get(0).getIpList()){
                logger.info("Adding join bucket to each IP address");
            if(originalQuery.getOutput().getBrokerName()!=null)
            {
                remoteBucketDeployer.deploy(originalQuery.getOutput().getBrokerName(),ip);
            }
            bucketMap.put(ip,subBucket);
        }
        return  bucketMap;
    }





   }
