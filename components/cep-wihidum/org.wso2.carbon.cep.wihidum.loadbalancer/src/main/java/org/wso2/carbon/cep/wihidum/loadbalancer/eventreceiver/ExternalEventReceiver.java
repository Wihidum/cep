package org.wso2.carbon.cep.wihidum.loadbalancer.eventreceiver;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.Divider;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.DividerFactory;
import org.wso2.carbon.databridge.commons.Event;

import java.util.List;


public class ExternalEventReceiver {
    private static Logger log = Logger.getLogger(ExternalEventReceiver.class);
    private DividerFactory dividerFactory;
    private Divider divider;
    private int count;


    public ExternalEventReceiver() {
        dividerFactory = DividerFactory.getInstances();
        divider = dividerFactory.getDivider();
    }


    public void receiveEventBundle(List<Event> eventList) {
        divider.divide(eventList);
        count = count + eventList.size();
    }


}
