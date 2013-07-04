package org.wso2.siddhi.loadbalancer;

import org.wso2.siddhi.loadbalancer.eventreceiver.ExternalEventReceiver;


public class SiddhiLBManager {


    public static void main(String[] args) {
        ExternalEventReceiver.startReciver("localhost", 7611);
    }

}
