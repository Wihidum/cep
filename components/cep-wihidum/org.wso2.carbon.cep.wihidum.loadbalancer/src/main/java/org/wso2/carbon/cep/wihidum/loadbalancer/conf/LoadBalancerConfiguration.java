package org.wso2.carbon.cep.wihidum.loadbalancer.conf;


public class LoadBalancerConfiguration {

    private boolean loadbalanceron;
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isLoadbalanceron() {
        return loadbalanceron;
    }

    public void setLoadbalanceron(boolean loadbalanceron) {
        this.loadbalanceron = loadbalanceron;
    }
}
