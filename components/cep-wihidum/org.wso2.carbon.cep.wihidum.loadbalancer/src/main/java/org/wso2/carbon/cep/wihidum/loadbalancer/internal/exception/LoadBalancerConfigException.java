package org.wso2.carbon.cep.wihidum.loadbalancer.internal.exception;


public class LoadBalancerConfigException extends Exception {


    public  LoadBalancerConfigException(){

    }

    public  LoadBalancerConfigException(String message){
        super(message);
    }

    public  LoadBalancerConfigException(String message ,Throwable cause){
        super(message,cause);
    }

    public  LoadBalancerConfigException(Throwable cause){
        super(cause);
    }




}
