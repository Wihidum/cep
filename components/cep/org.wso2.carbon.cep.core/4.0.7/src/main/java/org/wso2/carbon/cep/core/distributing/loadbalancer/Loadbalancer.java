package org.wso2.carbon.cep.core.distributing.loadbalancer;


import java.util.ArrayList;
import java.util.List;

public class Loadbalancer {

    private String ip;
    private List<LBOutputNode>  outputNodeList = new ArrayList<LBOutputNode>();

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
}
