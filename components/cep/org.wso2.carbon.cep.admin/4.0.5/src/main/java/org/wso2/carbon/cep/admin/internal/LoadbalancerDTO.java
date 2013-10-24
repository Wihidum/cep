package org.wso2.carbon.cep.admin.internal;


public class LoadbalancerDTO {

   private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



    private LBOutputNodeDTO[] lbOutputNodeDTOs;

    public LBOutputNodeDTO[] getLbOutputNodeDTOs() {
        return lbOutputNodeDTOs;
    }

    public void setLbOutputNodeDTOs(LBOutputNodeDTO[] lbOutputNodeDTOs) {
        this.lbOutputNodeDTOs = lbOutputNodeDTOs;
    }
}
