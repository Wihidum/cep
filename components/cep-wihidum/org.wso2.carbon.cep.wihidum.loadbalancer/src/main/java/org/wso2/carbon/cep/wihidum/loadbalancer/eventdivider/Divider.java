package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider;


import org.wso2.carbon.databridge.commons.Event;

import java.util.List;


public interface Divider {

    public void divide(List<Event> eventList);

}
