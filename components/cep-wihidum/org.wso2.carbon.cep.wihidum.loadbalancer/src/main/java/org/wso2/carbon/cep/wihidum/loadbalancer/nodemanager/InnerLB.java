package org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager;


import java.util.ArrayList;
import java.util.List;

public class InnerLB {

    private String type;
    private  String id;
    private List<String> outputNodeIdList = new ArrayList<String>();


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

    public List<String> getOutputNodeIdList() {
        return outputNodeIdList;
    }

    public void addOutputNodeId(String id) {
        this.outputNodeIdList.add(id);
    }

    public void clear(){
        outputNodeIdList.clear();
    }
}
