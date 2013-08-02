package org.wso2.carbon.cep.wihidum.loadbalancer.utils;


import org.wso2.carbon.databridge.commons.Event;

import java.util.ArrayList;
import java.util.List;

public class EventComposite {
    private List<Event> eventList = new ArrayList<Event>();
    private int nodeIndex;

    public EventComposite(List<Event> eventList, int nodeIndex) {
        this.eventList.addAll(eventList);
        this.nodeIndex = nodeIndex;

    }

    public List<Event> getEventList() {
        return eventList;
    }

    public int getNodeIndex() {
        return nodeIndex;
    }

}
