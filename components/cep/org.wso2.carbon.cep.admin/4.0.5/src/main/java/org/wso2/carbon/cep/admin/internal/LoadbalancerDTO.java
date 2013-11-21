package org.wso2.carbon.cep.admin.internal;


public class LoadbalancerDTO {

    private String ip;
    private String type;
    private StreamDTO[] streamDTOs;
    private InnerOutputNodesDTO[] innerOutputNodesDTOs;
    private LBOutputNodeDTO[] lbOutputNodeDTOs;

    public InnerOutputNodesDTO[] getInnerOutputNodesDTOs() {
        return innerOutputNodesDTOs;
    }

    public void setInnerOutputNodesDTOs(InnerOutputNodesDTO[] innerOutputNodesDTOs) {
        this.innerOutputNodesDTOs = innerOutputNodesDTOs;
    }

    public StreamDTO[] getStreamDTOs() {
        return streamDTOs;
    }

    public void setStreamDTOs(StreamDTO[] streamDTOs) {
        this.streamDTOs = streamDTOs;
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





    public LBOutputNodeDTO[] getLbOutputNodeDTOs() {
        return lbOutputNodeDTOs;
    }

    public void setLbOutputNodeDTOs(LBOutputNodeDTO[] lbOutputNodeDTOs) {
        this.lbOutputNodeDTOs = lbOutputNodeDTOs;
    }
}
