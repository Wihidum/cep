package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl;

import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.siddhi.query.api.QueryFactory;
import org.wso2.siddhi.query.api.query.Query;

import java.util.List;


public class EventStreamDivider implements Divider {
    final static Query query = QueryFactory.createQuery();




    @Override
    public void divide(List<Event> eventList) {

    }
}
