package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue.EventQueue;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConstants;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.databridge.commons.Event;


import java.util.ArrayList;
import java.util.List;


public class EventRRDivider implements Divider {
    private static Logger logger = Logger.getLogger(EventRRDivider.class);
    private static List<Node> nodelist;
    private int eventCount;
    private int nodeCount;
    private EventQueue eventQueue;
    private List<Event> outputEventList = new ArrayList<Event>();
    private LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();


    public EventRRDivider() {
        nodelist = loadBalancerConfiguration.getNodeList();
        eventQueue = new EventQueue(nodelist);
    }


    @Override
    public synchronized void divide(List<Event> eventList) {
        outputEventList.addAll(eventList);
        eventCount = eventCount + eventList.size();
        if (eventCount >= loadBalancerConfiguration.getEventDivideCount()) {
            EventComposite eventComposite = new EventComposite(outputEventList, nodeCount);
            nodeCount++;
            outputEventList.clear();
            eventCount = LoadBalancerConstants.COUNTER_BEGIN_VALUE;
            if (nodeCount == nodelist.size()) {
                nodeCount = LoadBalancerConstants.COUNTER_BEGIN_VALUE;
            }
            eventQueue.addEventBundle(eventComposite);
        }

    }


}
