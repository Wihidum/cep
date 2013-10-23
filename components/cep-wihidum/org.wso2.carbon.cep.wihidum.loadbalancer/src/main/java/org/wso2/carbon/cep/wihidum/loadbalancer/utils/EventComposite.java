package org.wso2.carbon.cep.wihidum.loadbalancer.utils;


import org.wso2.carbon.databridge.commons.Event;

import java.util.ArrayList;
import java.util.List;

public class EventComposite {
    private List<Event> eventList = new ArrayList<Event>();
    private int nodeIndex;
    private String nodeId;

    public EventComposite(List<Event> eventList, int nodeIndex) {
        this.eventList.addAll(eventList);
        this.nodeIndex = nodeIndex;

    }

    public EventComposite(List<Event> eventList, String id) {
        this.eventList = eventList;
        this.nodeId = id;
    }

     public EventComposite(List<Event> eventList){
        this.eventList = eventList;
     }

    public List<Event> getEventList() {
        return eventList;
    }

    public int getNodeIndex() {
        return nodeIndex;
    }

    public String getNodeId(){
        return nodeId;
    }

    public String GetStreamId(){
        return eventList.get(0).getStreamId();
    }
}
