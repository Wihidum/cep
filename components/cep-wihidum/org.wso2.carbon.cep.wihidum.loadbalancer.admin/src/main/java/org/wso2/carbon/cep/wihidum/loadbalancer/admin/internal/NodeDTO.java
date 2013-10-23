package org.wso2.carbon.cep.wihidum.loadbalancer.admin.internal;


public class NodeDTO {

    private String hostname;
    private String port;

    public NodeDTO(String hostname,String port){
        this.hostname = hostname;
        this.port = port;

    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
