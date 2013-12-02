package org.wso2.carbon.cep.core.distributing.loadbalancer;


import java.util.ArrayList;
import java.util.List;

public class Loadbalancer {

    private String ip;
    private String type;
    private List<LBOutputNode>  outputNodeList = new ArrayList<LBOutputNode>();
    private  List<Stream> streamList = new ArrayList<Stream>();
    private  List<InnerOutputNode> innerOutputNodeList = new ArrayList<InnerOutputNode>();

    public List<InnerOutputNode> getInnerOutputNodeList() {
        return innerOutputNodeList;
    }

    public void addInnerOutputNode(InnerOutputNode innerOutputNode) {
        this.innerOutputNodeList.add(innerOutputNode);
    }

    public List<Stream> getStreamList() {
        return streamList;
    }

    public void addStream(Stream stream) {
        this.streamList.add(stream);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<LBOutputNode> getOutputNodeList() {
        return outputNodeList;
    }

    public void addOutputNode(LBOutputNode outputNodeList) {
        this.outputNodeList.add(outputNodeList);
    }

    public void setOutputNodeList(List<LBOutputNode> outputList){
        this.outputNodeList = outputList;
    }

    public void setInnerOutputNodeList(List<InnerOutputNode> innerOutputList){
        this.innerOutputNodeList = innerOutputList;
    }

    public void setStreamList(List<Stream> streamList){
        this.streamList = streamList;
    }
}
