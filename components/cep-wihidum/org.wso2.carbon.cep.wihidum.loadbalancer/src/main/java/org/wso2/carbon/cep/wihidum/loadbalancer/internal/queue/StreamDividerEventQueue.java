package org.wso2.carbon.cep.wihidum.loadbalancer.internal.queue;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.EventComposite;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamDividerEventQueue {
    private static Logger logger = Logger.getLogger(EventQueue.class);
    private BlockingQueue<EventComposite> eventQueue;
    private ExecutorService executorService;
    private List<Node> endPoints;

    public StreamDividerEventQueue(List<Node> endPoints) {
        this.endPoints = endPoints;
        eventQueue = new ArrayBlockingQueue<EventComposite>(LoadBalancerConfiguration.getInstance().getBlockingQueueCapacity());
        executorService = Executors.newFixedThreadPool(LoadBalancerConfiguration.getInstance().getQueueWorkerThreads());
    }

    public void addEventBundle(EventComposite eventComposite) {
        try {
            eventQueue.put(eventComposite);
        } catch (InterruptedException e) {
            logger.info("Cannot add Events to eventQueue");
        }
        executorService.submit(new StreamDividerQueueWorker(endPoints, eventQueue));
    }

    protected void finalize() throws Throwable {
        logger.info("Thread Executor finalizing");
        executorService.shutdown();
        super.finalize();
    }
}
