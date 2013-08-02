package org.wso2.carbon.cep.wihidum.loadbalancer;


import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventpublisher.util.StreamDefinitionProvider;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventreceiver.ExternalEventReceiver;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import java.util.List;


public class SiddhiLBManager {
    private static Logger logger = Logger.getLogger(SiddhiLBManager.class);
    private static SiddhiLBManager siddhiLBManager;
    private LoadBalancerConfiguration loadBalancerConfiguration;
    private ExternalEventReceiver externalEventReceiver;

    private SiddhiLBManager() {
        loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
        externalEventReceiver = new ExternalEventReceiver();
    }


    public static SiddhiLBManager getSiddhiLBManager() {
        if (siddhiLBManager == null) {
            siddhiLBManager = new SiddhiLBManager();
        }
        return siddhiLBManager;
    }


    public void stopLoadBalancer() {
        LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
        List<Node> nodeList = loadBalancerConfiguration.getNodeList();
        for (Node node : nodeList) {
            node.getEventPublisher().stop();
        }
    }

    public LoadBalancerConfiguration loadBalancerConfiguration() {
        return loadBalancerConfiguration;
    }

    public void receiveEventBundle(List<Event> eventList) {
        externalEventReceiver.receiveEventBundle(eventList);
    }

    public void addStreamDefinition(StreamDefinition streamDefinition) {
        StreamDefinitionProvider.addStreamDefinition(streamDefinition);
    }

}
