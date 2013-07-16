package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider;


import org.wso2.carbon.databridge.commons.Event;


public interface Divider {
    public  void divide(Event event);

}
