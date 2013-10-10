package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl;

import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;
import org.wso2.carbon.databridge.commons.Event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EventJoinDivider implements Divider,eventSender {

    List<String> nodeIdList;

    public EventJoinDivider(List<String> nodeIdList){
        this.nodeIdList = nodeIdList;
    }

    @Override
    public void divide(List<Event> eventList) {
        LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
        ConcurrentHashMap<String, eventSender> senderMap = loadBalancerConfiguration.getSenderMap();

    }

    @Override
    public void sendEvents(List<Event> eventList) throws EventPublishException {
        divide(eventList);
    }
}
