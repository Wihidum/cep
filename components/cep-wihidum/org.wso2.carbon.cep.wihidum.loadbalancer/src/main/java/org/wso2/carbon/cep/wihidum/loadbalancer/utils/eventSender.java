package org.wso2.carbon.cep.wihidum.loadbalancer.utils;

import java.util.List;

import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.databridge.commons.Event;

public interface eventSender {
    public void sendEvents(List<Event> eventList) throws EventPublishException;
}
