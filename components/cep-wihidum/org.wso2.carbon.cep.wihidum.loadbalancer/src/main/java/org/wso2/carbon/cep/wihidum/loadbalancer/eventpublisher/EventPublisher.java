package org.wso2.carbon.cep.wihidum.loadbalancer.eventpublisher;

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.*;

import java.net.MalformedURLException;
import java.util.List;


public class EventPublisher {
    private static Logger logger = Logger.getLogger(EventPublisher.class);
    private AgentConfiguration agentConfiguration;
    private Agent agent;
    private DataPublisher dataPublisher;
    private int count;


    public EventPublisher(String host, String port) throws MalformedURLException, AgentException, AuthenticationException, TransportException {
        agentConfiguration = new AgentConfiguration();
        agent = new Agent(agentConfiguration);
        try {

            dataPublisher = new DataPublisher("tcp://" + host + ":" + port, "admin", "admin", agent);
            logger.info("DataPublisher Created For LB");
        } catch (MalformedURLException e) {
            logger.info(e.getMessage(), e);
            throw new MalformedURLException(e.getMessage());
        } catch (AgentException e) {
            logger.info(e.getMessage(), e);
            throw new AgentException(e.getMessage(), e);
        } catch (AuthenticationException e) {
            logger.info(e.getMessage());
            throw new AuthenticationException(e.getMessage(), e);
        } catch (TransportException e) {
            logger.info(e.getMessage());
            throw new TransportException(e.getMessage(), e);
        }

    }


    public synchronized void publishEvents(List<Event> eventList) throws DifferentStreamDefinitionAlreadyDefinedException, MalformedStreamDefinitionException, AgentException, StreamDefinitionException {
         count = count +eventList.size();
        for (Event event : eventList) {
            try {
                dataPublisher.publish(event.getStreamId(), event.getMetaData(), event.getCorrelationData(),event.getPayloadData());
            } catch (AgentException e) {
                logger.info(e.getMessage());
                throw new AgentException(e.getMessage());
            }
        }
    }

    public void stop() {
        dataPublisher.stop();
    }


}
