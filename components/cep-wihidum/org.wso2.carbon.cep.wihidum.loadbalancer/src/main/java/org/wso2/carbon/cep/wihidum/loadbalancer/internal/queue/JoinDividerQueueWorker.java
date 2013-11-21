package org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl.util.GeneralHashFunctionLibrary;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl.util.HashFactory;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;
import org.wso2.carbon.databridge.commons.Event;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class JoinDividerQueueWorker  implements Runnable {

    private BlockingQueue<EventComposite> eventQueue;
    private List<String> nodeIdList;
    private static Logger logger = Logger.getLogger(QueueWorker.class);
    private LoadBalancerConfiguration loadBalancerConfiguration = LoadBalancerConfiguration.getInstance();
    private ConcurrentHashMap<String, List<String>> ESDConfig;
    private ConcurrentHashMap<String, eventSender> senderMap;

    public JoinDividerQueueWorker(List<String> nodeIdList, BlockingQueue<EventComposite> eventQueue) {
        this.eventQueue = eventQueue;
        this.nodeIdList = nodeIdList;
        senderMap = loadBalancerConfiguration.getSenderMap();
    }


    @Override
    public void run() {
        EventComposite eventComposite = eventQueue.poll();
        List<Event> eventList = eventComposite.getEventList();

        HashFactory hashFactory = new GeneralHashFunctionLibrary();
        int nodeIdListSize = nodeIdList.size();
        for(Event event : eventList){

            long hashValue = hashFactory.RSHash(event.getPayloadData()[0].toString());

            int nodeToSend = (int) (hashValue%nodeIdListSize);

            eventSender sender = senderMap.get(nodeIdList.get(nodeToSend));
            Node node = null;
            if (sender instanceof Node){
                node = (Node) sender;
            }
            try {
                node.addEvent(event);
            } catch (EventPublishException e) {
                e.printStackTrace();
            }
        }
    }
}