package org.wso2.carbon.cep.wihidum.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.brokermanager.core.BrokerManagerService;
import org.wso2.carbon.cep.core.CEPServiceInterface;
import org.wso2.carbon.cep.core.distributing.RemoteBucketHelper;
import org.wso2.carbon.cep.core.distributing.WihidumValueHolder;
import org.wso2.carbon.cep.wihidum.core.bucket.RemoteBucketDeployManager;
import org.wso2.carbon.cep.wihidum.core.cluster.ClusterManager;

/**
 * @scr.component name="wihidumcoreservice.component" immediate="true"
 * @scr.reference name="brokermanager.service"
 * interface="org.wso2.carbon.brokermanager.core.BrokerManagerService" cardinality="1..1"
 * policy="dynamic" bind="setBrokerManagerService" unbind="unSetBrokerManagerService"
 * @scr.reference name="cep.service"
 * interface="org.wso2.carbon.cep.core.CEPServiceInterface" cardinality="1..1"
 * policy="dynamic" bind="setCEPService" unbind="unSetCEPService"
 **/
public class WihidumCoreDS {
    private static final Log log = LogFactory.getLog(WihidumCoreDS.class);
    private WihidumValueHolder wihidumValueHolder;


    protected void activate(ComponentContext context){
        try{
         ClusterManager clusterManager = ClusterManager.getInstant();
         clusterManager.initiate();
            wihidumValueHolder = WihidumValueHolder.getInstance();
            wihidumValueHolder.addRemoteObject(new RemoteBucketDeployManager());
            RemoteBucketHelper.executeRemoteBucketDeploy();

        log.info("Successfully initiated cluster manager");
        }catch (Throwable e){
            log.error("Can not initiate cluster service ", e);
        }
    }

    public void setBrokerManagerService(BrokerManagerService brokerManagerService) {
        WihidumCoreValueHolder.getInstance().setBrokerManagerService(brokerManagerService);
    }

    public void unSetBrokerManagerService(BrokerManagerService brokerManagerService) {
        WihidumCoreValueHolder.getInstance().unsetBrokerManagerService();
    }
    public void setCEPService(CEPServiceInterface cepService) {
        WihidumCoreValueHolder.getInstance().setCEPService(cepService);
    }

    public void unSetCEPService(CEPServiceInterface cepService) {
        WihidumCoreValueHolder.getInstance().unsetCEPService();
    }
}
