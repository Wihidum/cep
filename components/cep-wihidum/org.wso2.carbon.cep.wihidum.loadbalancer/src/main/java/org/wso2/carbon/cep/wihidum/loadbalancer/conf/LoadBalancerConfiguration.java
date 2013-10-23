package org.wso2.carbon.cep.wihidum.loadbalancer.conf;


import org.apache.axiom.om.OMElement;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventdivider.impl.EventRRDivider;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.LoadBalancerConfigException;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConfBuilder;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConstants;
import org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager.Node;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.utils.eventSender;

public class LoadBalancerConfiguration {
    private static Logger logger = Logger.getLogger(LoadBalancerConfiguration.class);
    private static LoadBalancerConfiguration loadBalancerConfiguration;
    private boolean loadbalanceron;
    private int port;
    private boolean roundRobin;
    private boolean eventStream;
    private boolean streamDivide;
    private int reciverbundlesize;
    private int eventDivideCount;
    private List<Node> nodeList = new ArrayList<Node>();
    //node map implementation explanation can be located at
    //http://howtodoinjava.com/2013/05/27/best-practices-for-using-concurrenthashmap/
    //senderMap will hold both node and RRD objects
    private ConcurrentHashMap<String, eventSender> senderMap = new ConcurrentHashMap<String, eventSender>(16, 0.9f, 4);
    private ConcurrentHashMap<String, List<String>> ESDConfig = new ConcurrentHashMap<String, List<String>>(16, 0.9f, 2);
    private String host;
    private int blockingQueueCapacity;
    private int queueWorkerThreads;
    //to store streamId, nodes relations
    //in the form of steamId -> nodeId
    //TODO change this to a nodeId arraylist to support event duplication
    private Hashtable<String, Integer> adjMatrix;


    public Hashtable<String, Integer> getAdjMatrix(){
       return adjMatrix;
    }

    public int getQueueWorkerThreads() {
        return queueWorkerThreads;
    }

    public void setQueueWorkerThreads(int queueWorkerThreads) {
        this.queueWorkerThreads = queueWorkerThreads;
    }

    public int getBlockingQueueCapacity() {
        return blockingQueueCapacity;
    }

    public void setBlockingQueueCapacity(int blockingQueueCapacity) {
        this.blockingQueueCapacity = blockingQueueCapacity;
    }


    public boolean isStreamDivide() {
        return streamDivide;
    }

    public void setStreamDivide(boolean streamDivide) {
        this.streamDivide = streamDivide;
    }

    public void addOutputNode(String host, String port) {
        nodeList.add(new Node(host, port));
        //node id is calculated here
        //TODO change this to set node id from configuration file
        senderMap.put(host + ":" + port, new Node(host, port));
    }

    public void addRRDconfig(String id, List<String> nodeIdList){
        senderMap.put(id, new EventRRDivider());
    }

    public void addESDConfig(String streamID, List<String> senderIdList){
        ESDConfig.put(streamID, senderIdList);
    }

    public ConcurrentHashMap<String, List<String>> getESDConfig(){
        return ESDConfig;
    }

    public  ConcurrentHashMap<String, eventSender> getSenderMap(){
        return senderMap;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public int getEventDivideCount() {
        return eventDivideCount;
    }

    public void setEventDivideCount(int eventDivideCount) {
        this.eventDivideCount = eventDivideCount;
    }

    public int getReciverbundlesize() {
        return reciverbundlesize;
    }

    public void setReciverbundlesize(int reciverbundlesize) {
        this.reciverbundlesize = reciverbundlesize;
    }

    LoadBalancerConfiguration() {

    }

    public int getPort() {
        return port;
    }

    public void setRoundRobin(boolean roundRobin) {
        this.roundRobin = roundRobin;
    }

    public void setEventStream(boolean eventStream) {
        this.eventStream = eventStream;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isLoadbalanceron() {
        return loadbalanceron;
    }

    public void setLoadbalanceron(boolean loadbalanceron) {
        this.loadbalanceron = loadbalanceron;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isRoundRobin() {
        return this.roundRobin;

    }


     public boolean isEventStream() {
        return this.eventStream;

    }

    public void clearOutputNodes(){
        nodeList.clear();
    }

    public static LoadBalancerConfiguration getInstance() {
        if (loadBalancerConfiguration == null) {
            OMElement omElement = null;
            try {
                omElement = LoadBalancerConfBuilder.loadConfigXML();
            } catch (LoadBalancerConfigException e) {
                logger.info("Cannot Read loadbalancer-conf.xml");
            }
            loadBalancerConfiguration = LoadBalancerConfigHelper.fromOM(omElement);
            if (loadBalancerConfiguration == null) {
                loadBalancerConfiguration = new LoadBalancerConfiguration();
                loadBalancerConfiguration.setEventDivideCount(LoadBalancerConstants.DEFAULT_MAX_BUFFERED_EVENT_COUNT);
                loadBalancerConfiguration.setReciverbundlesize(LoadBalancerConstants.DEFAULT_RECIVER_BUNDLE_SIZE);
                loadBalancerConfiguration.setPort(LoadBalancerConstants.DEFAULT_PORT);
                loadBalancerConfiguration.setRoundRobin(LoadBalancerConstants.DEFAULT_METHOD_ROUND_ROBIN);
                loadBalancerConfiguration.setStreamDivide(false);
                loadBalancerConfiguration.setLoadbalanceron(false);
                loadBalancerConfiguration.setHost(LoadBalancerConstants.DEFAULT_RUNNING_HOST);
            }
        }
        return loadBalancerConfiguration;
    }
}
