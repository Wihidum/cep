package org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager;

import org.wso2.carbon.cep.wihidum.loadbalancer.eventpublisher.EventPublisher;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.*;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class Node implements eventSender {

    private String hostname;
    private String port;
    private String streamID; // this should be removed as this is redundant with adjList
    private List<Event> eventList = new ArrayList<Event>();
    private EventPublisher eventPublisher;
    private static Logger logger = Logger.getLogger(Node.class);

    public Node(String hostName, String port) {
        this.hostname = hostName;
        this.port = port;
        try {
            eventPublisher = new EventPublisher(hostName, port);
        } catch (Exception e) {
            logger.info("Error when creating event publisher");
        }
    }

    public Node(String hostName, String port, String streamID) {
        this.hostname = hostName;
        this.port = port;
        this.streamID = streamID;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public void setStreamID(String streamID) {
        this.streamID = streamID;
    }

    public String getStreamID() {
        return streamID;
    }

    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void addEventList(List<Event> eventList) throws EventPublishException {
        try {
            eventPublisher.updateEventPublisher();
            eventPublisher.publishEvents(eventList);

        } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
            logger.info(e.getMessage());
            throw new EventPublishException(e.getMessage(), e);
        } catch (MalformedStreamDefinitionException e) {
            logger.info(e.getMessage());
            throw new EventPublishException(e.getMessage(), e);
        } catch (AgentException e) {
            logger.info(e.getMessage());
            throw new EventPublishException(e.getMessage(), e);
        } catch (StreamDefinitionException e) {
            logger.info(e.getMessage());
            throw new EventPublishException(e.getMessage(), e);
        }

    }

    public void addEvent(Event event) throws EventPublishException {
        eventList.add(event);

        if (eventList.size()>=1000){
            try {
                eventPublisher.updateEventPublisher();
                eventPublisher.publishEvents(eventList);
            } catch (DifferentStreamDefinitionAlreadyDefinedException e) {
                logger.info(e.getMessage());
                throw new EventPublishException(e.getMessage(), e);
            } catch (MalformedStreamDefinitionException e) {
                logger.info(e.getMessage());
                throw new EventPublishException(e.getMessage(), e);
            } catch (AgentException e) {
                logger.info(e.getMessage());
                throw new EventPublishException(e.getMessage(), e);
            } catch (StreamDefinitionException e) {
                logger.info(e.getMessage());
                throw new EventPublishException(e.getMessage(), e);
            }
            eventList.clear();
        }

    }

    @Override
    public void sendEvents(List<Event> eventList) throws EventPublishException  {
        addEventList(eventList);
    }
}
