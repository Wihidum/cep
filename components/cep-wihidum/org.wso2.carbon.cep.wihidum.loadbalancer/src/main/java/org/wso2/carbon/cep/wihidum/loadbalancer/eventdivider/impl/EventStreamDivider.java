package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl;

import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue.StreamDividerEventQueue;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConstants;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.databridge.commons.Event;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class EventStreamDivider implements Divider {
    
    private static List<Node> nodelist;
    private int eventCount;
    private int nodeCount;
    private static volatile StreamDividerEventQueue eventQueue = null;
    private List<Event> outputEventList = new ArrayList<Event>();


    public EventStreamDivider() {
//        nodelist = loadBalancerConfiguration.getNodeList();
//        eventQueue = new StreamDividerEventQueue(nodelist);

    }

    @Override
    public void divide(List<Event> eventList) {
        if (eventQueue == null){
            eventQueue = new StreamDividerEventQueue();
        }
        eventQueue.addEventBundle(new EventComposite(eventList));
    }
}
