package org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue;


import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;
import org.wso2.carbon.databridge.commons.Event;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class QueueWorker implements Runnable {

    private List<String> nodeIdList;
    private BlockingQueue<EventComposite> eventQueue;
    private static Logger logger = Logger.getLogger(QueueWorker.class);
    private LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
    private ConcurrentHashMap<String, eventSender> senderMap;

    public QueueWorker(List<String> nodeIdList, BlockingQueue<EventComposite> eventQueue) {
        this.eventQueue = eventQueue;
        this.nodeIdList = nodeIdList;
        senderMap = loadBalancerConfiguration.getSenderMap();
    }


    @Override
    public void run() {
        EventComposite eventComposite = eventQueue.poll();
        int index = eventComposite.getNodeIndex();
        List<Event> eventList = eventComposite.getEventList();
        String senderId = nodeIdList.get(index);
        Node node = (Node) senderMap.get(senderId);
        //TODO remove debugging code
//        System.out.println("streamID = " + eventComposite.GetStreamId() + " routed to node = " + node.getHostname() + ":" + node.getPort() + " on RRD");
        try {
            node.addEventList(eventList);
        }catch (EventPublishException e) {
            logger.info("EventPublish Error" + e.getMessage());
        }

    }
}
