package org.wso2.carbon.cep.wihidum.core.internal;


import org.wso2.carbon.brokermanager.core.BrokerManagerService;

public class WihidumCoreValueHolder {
    private static WihidumCoreValueHolder wihidumCoreValueHolder = new WihidumCoreValueHolder();
    private BrokerManagerService brokerManagerService;

    private WihidumCoreValueHolder(){
        //Private constructor to implement singleton
    }

    public static WihidumCoreValueHolder getInstance() {
        if (wihidumCoreValueHolder == null) {
            wihidumCoreValueHolder = new WihidumCoreValueHolder();
        }
        return wihidumCoreValueHolder;
    }

    public void setBrokerManagerService(BrokerManagerService brokerManagerService) {
        this.brokerManagerService = brokerManagerService;
    }

    public void unsetBrokerManagerService() {
        this.brokerManagerService = null;
    }

    public BrokerManagerService getBrokerManagerService() {
        return brokerManagerService;
    }
}
