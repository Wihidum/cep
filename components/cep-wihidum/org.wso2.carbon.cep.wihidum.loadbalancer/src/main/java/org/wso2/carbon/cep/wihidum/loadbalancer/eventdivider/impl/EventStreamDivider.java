package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl;

import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue.StreamDividerEventQueue;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConstants;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.siddhi.query.api.QueryFactory;
import org.wso2.siddhi.query.api.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class EventStreamDivider implements Divider {
    final static Query query = QueryFactory.createQuery();
    private static List<Node> nodelist;
    private int eventCount;
    private int nodeCount;
    private StreamDividerEventQueue eventQueue;
    private List<Event> outputEventList = new ArrayList<Event>();
    private LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
    private ConcurrentHashMap<String, List<String>> ESDConfig;


    public EventStreamDivider() {
        nodelist = loadBalancerConfiguration.getNodeList();
        eventQueue = new StreamDividerEventQueue(nodelist);

    }

    @Override
    public void divide(List<Event> eventList) {
        eventQueue.addEventBundle(new EventComposite(outputEventList));
    }
}
