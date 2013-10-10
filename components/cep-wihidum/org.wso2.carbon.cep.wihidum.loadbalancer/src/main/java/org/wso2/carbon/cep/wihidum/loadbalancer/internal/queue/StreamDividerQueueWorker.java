package org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;
import org.wso2.carbon.databridge.commons.Event;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class StreamDividerQueueWorker  implements Runnable {

    private BlockingQueue<EventComposite> eventQueue;
    private static Logger logger = Logger.getLogger(QueueWorker.class);
    private LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
    private ConcurrentHashMap<String, List<String>> ESDConfig;
    private ConcurrentHashMap<String, eventSender> senderMap;

    public StreamDividerQueueWorker(BlockingQueue<EventComposite> eventQueue) {
        this.eventQueue = eventQueue;
        ESDConfig =  loadBalancerConfiguration.getESDConfig();
        senderMap = loadBalancerConfiguration.getSenderMap();
    }


    @Override
    public void run() {
        EventComposite eventComposite = eventQueue.poll();
        List<Event> eventList = eventComposite.getEventList();
        List<String> senderIds = ESDConfig.get(eventComposite.GetStreamId());
        for (String senderId : senderIds){
            eventSender sender = senderMap.get(senderId);
            System.out.println("streamID = " + eventComposite.GetStreamId() + " routed to sender = " + senderId + " on ESD");
            try {
                sender.sendEvents(eventList);
            }catch (EventPublishException e) {
                logger.info("EventPublish Error" + e.getMessage());
            }
        }
        /*for (Event evt : eventList){
            int nodeId = loadBalancerConfiguration.getAdjMatrix().get(evt.getStreamId());
            //TODO need to handle the case where given streamId is not added to the adjMatrix
            Node node = nodeList.get(nodeId);
            try {
                node.addEvent(evt);
            }catch (EventPublishException e) {
                logger.info("EventPublish Error" + e.getMessage());
            }
        }*/

    }
}
