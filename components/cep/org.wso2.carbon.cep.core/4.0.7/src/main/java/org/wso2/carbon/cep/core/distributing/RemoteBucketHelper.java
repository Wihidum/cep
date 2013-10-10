package org.wso2.carbon.cep.core.distributing;

import org.apache.log4j.Logger;

public class RemoteBucketHelper {

    private static WihidumValueHolder wihidumValueHolder = WihidumValueHolder.getInstance();
    private static Logger logger = Logger.getLogger(RemoteBucketHelper.class);

 public static void executeRemoteBucketDeploy(){
      logger.info("executeRemoteBucketDeploy method run");
     DistributingWihidumValueHolder distributingWihidumValueHolder = wihidumValueHolder.getRemoteWihidum();
     if(distributingWihidumValueHolder !=null){
         distributingWihidumValueHolder.execute();
         logger.info("call for wihidum core execute method");

     }
 }


}
