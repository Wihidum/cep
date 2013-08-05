package org.wso2.carbon.cep.wihidum.loadbalancer.eventpublisher;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventpublisher.util.StreamDefinitionProvider;
import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.*;

import java.net.MalformedURLException;
import java.util.List;


public class EventPublisher {
    private static Logger logger = Logger.getLogger(EventPublisher.class);
    private DataPublisher dataPublisher;
    private int count;
    private String host;
    private String port;

    public EventPublisher(String host, String port) throws MalformedURLException, AgentException, AuthenticationException, TransportException {
        this.host = host;
        this.port = port;

    }


    public synchronized void publishEvents(List<Event> eventList) throws DifferentStreamDefinitionAlreadyDefinedException, MalformedStreamDefinitionException, AgentException, StreamDefinitionException {
        count = count + eventList.size();
        if (dataPublisher == null) {
            initDataPublisher(host, port);

        }
        for (Event event : eventList) {
            try {
                dataPublisher.publish(event);

            } catch (AgentException e) {
                logger.info(e.getMessage());
                throw new AgentException(e.getMessage());
            }
        }
    }

    public synchronized void updateEventPublisher() {
        if (dataPublisher == null) {
            initDataPublisher(host, port);
        }
        for (StreamDefinition streamDefinition : StreamDefinitionProvider.getStreamDefinitionList()) {
            try {
                dataPublisher.defineStream(streamDefinition);
            } catch (AgentException e){
                logger.info(e.getMessage(), e);
            } catch (StreamDefinitionException e) {
                logger.info(e.getMessage(), e);
            } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
                logger.info(e.getMessage(), e);
            } catch (MalformedStreamDefinitionException e) {
                logger.info(e.getMessage(), e);
            }
        }
    }


    private void initDataPublisher(String host, String port) {
        try {
            dataPublisher = new DataPublisher("tcp://" + host + ":" + port, "admin", "admin", (Agent) null);
            logger.info("DataPublisher Created For Wihidum Load Balancer");
        } catch (MalformedURLException e) {
            logger.info(e.getMessage(), e);

        } catch (AgentException e) {
            logger.info(e.getMessage(), e);

        } catch (AuthenticationException e) {
            logger.info(e.getMessage());

        } catch (TransportException e) {
            logger.info(e.getMessage());

        }

    }


    public void stop() {
        dataPublisher.stop();
    }


}
