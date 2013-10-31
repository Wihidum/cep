package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue.EventQueue;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConstants;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;
import org.wso2.carbon.databridge.commons.Event;


import java.util.ArrayList;
import java.util.List;


public class EventRRDivider implements Divider,eventSender {
    private static Logger logger = Logger.getLogger(EventRRDivider.class);
    private static List<Node> nodelist;
    List<String> nodeIdList;
    private int eventCount;
    private int nodeCount;
    private static volatile EventQueue eventQueue = null;
    private List<Event> outputEventList = new ArrayList<Event>();


    public EventRRDivider() {
//        nodelist = loadBalancerConfiguration.getNodeList();
//        eventQueue = new EventQueue(nodelist);
    }

    public EventRRDivider(List<String> nodeIdList){
        this.nodeIdList = nodeIdList;
    }

    @Override
    public synchronized void divide(List<Event> eventList) {
//        outputEventList.addAll(eventList);
//        eventCount = eventCount + eventList.size();
//        if (eventCount >= loadBalancerConfiguration.getEventDivideCount()) {
        if (eventQueue == null){
            eventQueue = new EventQueue(nodeIdList);
        }
            EventComposite eventComposite = new EventComposite(eventList, nodeCount);
            nodeCount++;
//            outputEventList.clear();
//            eventCount = LoadBalancerConstants.COUNTER_BEGIN_VALUE;
            if (nodeCount == nodeIdList.size()) {
                nodeCount = LoadBalancerConstants.COUNTER_BEGIN_VALUE;
            }
            eventQueue.addEventBundle(eventComposite);
//        }

    }

    @Override
    public void sendEvents(List<Event> eventList) throws EventPublishException {
        this.divide(eventList);
    }
}
