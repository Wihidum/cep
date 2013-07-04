package org.wso2.siddhi.loadbalancer.eventpublisher;

import org.apache.log4j.Logger;


import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.NoStreamDefinitionExistException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.siddhi.loadbalancer.utils.KeyStoreUtil;
import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;

import java.util.List;


public class EventPublisher {
    private static Logger logger = Logger.getLogger(EventPublisher.class);
    private static String STREAM_NAME = "org.wso2.phone.retail.store";
    private static String VERSION = "1.2.0";

    public static void publishEvents(String host, String port, List<Event> eventList) throws DifferentStreamDefinitionAlreadyDefinedException, MalformedStreamDefinitionException, AgentException, StreamDefinitionException {
        KeyStoreUtil.setTrustStoreParams();
        AgentConfiguration agentConfiguration = new AgentConfiguration();

        Agent agent = new Agent(agentConfiguration);
        DataPublisher dataPublisher = null;
        try {
            dataPublisher = new DataPublisher("tcp://" + host + ":" + port, "admin", "admin", agent);

        } catch (Exception e) {
            e.printStackTrace();
            dataPublisher.stop();
        }
        String streamId = null;

        try {
            streamId = dataPublisher.findStream(STREAM_NAME, VERSION);
        } catch (NoStreamDefinitionExistException e) {
            streamId = dataPublisher.defineStream("{" +
                    "  'name':'" + STREAM_NAME + "'," +
                    "  'version':'" + VERSION + "'," +
                    "  'nickName': 'Phone_Retail_Shop'," +
                    "  'description': 'Phone Sales'," +
                    "  'metaData':[" +
                    "          {'name':'clientType','type':'STRING'}" +
                    "  ]," +
                    "  'payloadData':[" +
                    "          {'name':'brand','type':'STRING'}," +
                    "          {'name':'quantity','type':'INT'}," +
                    "          {'name':'total','type':'INT'}," +
                    "          {'name':'buyer','type':'STRING'}" +
                    "  ]" +
                    "}");
//            //Define event stream
        }

        //System.out.println("Stream already defined");


        for (Event event : eventList) {
            try {
                dataPublisher.publish(streamId, event.getMetaData(), event.getCorrelationData(), event.getPayloadData());
                // logger.info("publishEvent");
            } catch (AgentException e) {
                e.printStackTrace();
                dataPublisher.stop();
            }
        }
        dataPublisher.stop();
    }


}
