package org.wso2.carbon.cep.wihidum.loadbalancer.admin.internal;

import java.util.ArrayList;
import java.util.List;

public class LoadbalancerDTO {


    private boolean loadbalanceron;
    private int port;
    private boolean roundRobin;
    private boolean streamDivide;
    private int reciverbundlesize;
    private int eventDivideCount;
    private NodeDTO[] nodeList;
    private String host;
    private int blockingQueueCapacity;
    private int queueWorkerThreads;
    private int nodeCount;


    public LoadbalancerDTO(int numberOFOutputNodes){
        nodeList = new NodeDTO[numberOFOutputNodes];
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

    public void addOutputNode(String host, String port){
        nodeList[nodeCount]=new NodeDTO(host, port);
        nodeCount++;
    }

    public NodeDTO[] getNodeList() {
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



    public int getPort() {
        return port;
    }

    public void setRoundRobin(boolean roundRobin) {
        this.roundRobin = roundRobin;
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



}
