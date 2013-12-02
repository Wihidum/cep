package org.wso2.carbon.cep.core.distributing.loadbalancer;


import java.util.ArrayList;
import java.util.List;

public class Stream {

    private String id;
    private List<InnerOutputNode> innerOutputNodeList = new ArrayList<InnerOutputNode>();
    public String getId() {
        return id;
    }


    public void setInnerOutputNodeList(List<InnerOutputNode> innerOutputNodeList) {
        this.innerOutputNodeList = innerOutputNodeList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<InnerOutputNode> getInnerOutputNodeList() {
        return this.innerOutputNodeList;
    }

    public void addInnerOutputNode(InnerOutputNode innerOutputNode) {
       this.innerOutputNodeList.add(innerOutputNode);

    }
}
