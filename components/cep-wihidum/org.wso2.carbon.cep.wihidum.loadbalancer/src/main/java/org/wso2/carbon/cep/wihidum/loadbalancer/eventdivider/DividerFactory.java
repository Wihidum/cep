package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider;


import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl.EventRRDivider;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl.EventStreamDivider;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;

import java.util.concurrent.ConcurrentHashMap;

public class DividerFactory {

    private static DividerFactory dividerFactory;

    private DividerFactory() {

    }


    public Divider getDivider() {
        LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
        Divider divider = null;
        if (loadBalancerConfiguration.isRoundRobin()) {
            ConcurrentHashMap<String, eventSender> senderMap = loadBalancerConfiguration.getSenderMap();
            divider = (Divider) senderMap.get(loadBalancerConfiguration.getRRDId());
        }
        else if (loadBalancerConfiguration.isEventStream()) {
            divider = new EventStreamDivider();
        }
        return divider;
    }


    public static DividerFactory getInstances() {
        if (dividerFactory == null) {
            dividerFactory = new DividerFactory();
        }
        return dividerFactory;
    }


}
