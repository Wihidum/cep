package org.wso2.carbon.cep.core.distributing;


public class WihidumValueHolder {

 private   DistributingWihidumValueHolder remoteWihidum;
 private static WihidumValueHolder wihidumValueHolder;


 private WihidumValueHolder(){

 }

 public void runDeploy(){

   if(remoteWihidum != null){
       remoteWihidum.execute();
   }
 }

  public static WihidumValueHolder getInstance(){
       if(wihidumValueHolder==null){
           wihidumValueHolder = new WihidumValueHolder();
       }
     return wihidumValueHolder;
  }
  public void addRemoteObject(Object object){
      remoteWihidum = (DistributingWihidumValueHolder)object;
  }
  public DistributingWihidumValueHolder getRemoteWihidum(){
     return  remoteWihidum;
  }


}
