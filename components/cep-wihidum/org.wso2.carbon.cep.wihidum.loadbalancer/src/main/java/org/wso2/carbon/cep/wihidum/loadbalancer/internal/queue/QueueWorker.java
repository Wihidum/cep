package org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue;


import org.wso2.carbon.cep.wihidum.loadbalancer.exception.EventPublishException;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;
import org.wso2.carbon.databridge.commons.Event;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

public class QueueWorker implements Runnable {

    private List<Node> nodeList;
    private BlockingQueue<EventComposite> eventQueue;
    private static Logger logger = Logger.getLogger(QueueWorker.class);

    public QueueWorker(List<Node> nodeList, BlockingQueue<EventComposite> eventQueue) {
        this.eventQueue = eventQueue;
        this.nodeList = nodeList;
    }


    @Override
    public void run() {
        EventComposite eventComposite = eventQueue.poll();
        int index = eventComposite.getNodeIndex();
        List<Event> eventList = eventComposite.getEventList();
        Node node = nodeList.get(index);
        try {
            node.addEventList(eventList);
        } catch (EventPublishException e) {
            logger.info("EventPublish Error" + e.getMessage());
        }

    }
}
