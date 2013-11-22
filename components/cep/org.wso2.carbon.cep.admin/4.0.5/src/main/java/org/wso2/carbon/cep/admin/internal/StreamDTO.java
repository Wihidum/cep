package org.wso2.carbon.cep.admin.internal;


public class StreamDTO {

private String id;
private InnerOutputNodesDTO[] innerOutputNodesDTOs;

    public InnerOutputNodesDTO[] getInnerOutputNodesDTOs() {
        return innerOutputNodesDTOs;
    }

    public void setInnerOutputNodesDTOs(InnerOutputNodesDTO[] innerOutputNodesDTOs) {
        this.innerOutputNodesDTOs = innerOutputNodesDTOs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
