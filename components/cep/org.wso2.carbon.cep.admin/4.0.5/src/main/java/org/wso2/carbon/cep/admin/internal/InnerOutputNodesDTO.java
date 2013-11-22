package org.wso2.carbon.cep.admin.internal;


public class InnerOutputNodesDTO {

    private String id;
    private String type;
    private LBOutputNodeDTO[] lbOutputNodeDTOs;


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

    public LBOutputNodeDTO[] getLbOutputNodeDTOs() {
        return lbOutputNodeDTOs;
    }

    public void setLbOutputNodeDTOs(LBOutputNodeDTO[] lbOutputNodeDTOs) {
        this.lbOutputNodeDTOs = lbOutputNodeDTOs;
    }
}
