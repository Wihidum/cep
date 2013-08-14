package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl;

import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue.EventQueue;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.siddhi.query.api.QueryFactory;
import org.wso2.siddhi.query.api.query.Query;

import java.util.ArrayList;
import java.util.List;


public class EventStreamDivider implements Divider {
    final static Query query = QueryFactory.createQuery();
    private static List<Node> nodelist;
    private int eventCount;
    private int nodeCount;
    private EventQueue eventQueue;
    private List<Event> outputEventList = new ArrayList<Event>();
    private LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();


    public EventStreamDivider() {
        nodelist = loadBalancerConfiguration.getNodeList();
        eventQueue = new EventQueue(nodelist);

    }

    @Override
    public void divide(List<Event> eventList) {
        String streamId = eventList.get(0).getStreamId();
           for(Node node:nodelist){
                if(node.getStreamID().equals(streamId)){
                    try {
                        node.addEventList(eventList);
                    } catch (EventPublishException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
    }
}
