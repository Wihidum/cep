package org.wso2.carbon.cep.wihidum.core.cluster;


import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.RemoteBucketDeployer;
import org.wso2.carbon.cep.core.exception.CEPConfigurationException;
import org.wso2.carbon.cep.wihidum.core.internal.WihidumCoreValueHolder;
import org.wso2.carbon.core.AbstractAdmin;

public class FaultHandler {
    private static FaultHandler faultHandler;
    private String localIP;
    private static final Log log = LogFactory.getLog(FaultHandler.class);

    private FaultHandler(){
        this.localIP = ClusterManager.getInstant().getLocalMemberAddress();
    }

    public FaultHandler getInstance(){
        if(faultHandler == null){
            faultHandler = new FaultHandler();
        }
        return faultHandler;
    }

    public void handle(String ipAddress){
        try {
            WihidumCoreValueHolder.getInstance().getCEPService().removeAllBuckets();
            String manager = (String)ClusterManager.getInstant().getBucketConfigurations().get("Manager");
            String deputyManager = (String)ClusterManager.getInstant().getBucketConfigurations().get("DeputyManager");
            if(ipAddress.equals(manager) && deputyManager.equals(localIP)){
                Bucket bucket;
                bucket = (Bucket)ClusterManager.getInstant().getBucketConfigurations().get(Constants.MASTER_BUCKET);
                RemoteBucketDeployer.deploy(localIP,bucket);
            }
        } catch (CEPConfigurationException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }
}

