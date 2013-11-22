package org.wso2.carbon.cep.core.distributing.loadbalancer;


import java.util.ArrayList;
import java.util.List;

public class InnerOutputNode {
    private String id;
    private String type;
    private List<LBOutputNode> lbOutputNodeList = new ArrayList<LBOutputNode>();

    public List<LBOutputNode> getLbOutputNodeList() {
        return lbOutputNodeList;
    }

    public void addLbOutputNode(LBOutputNode lbOutputNode) {
        lbOutputNodeList.add(lbOutputNode);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
