package org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider;


import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl.EventRRDivider;

public class DividerFactory {

    private static DividerFactory dividerFactory;

    private DividerFactory() {

    }


    public Divider getDivider() {
        LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
        Divider divider = null;
        if (loadBalancerConfiguration.isRoundRobin()) {
            divider = new EventRRDivider();
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
