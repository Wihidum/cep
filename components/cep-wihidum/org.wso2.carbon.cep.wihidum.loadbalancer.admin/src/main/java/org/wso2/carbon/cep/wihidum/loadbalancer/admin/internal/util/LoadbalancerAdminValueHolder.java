package org.wso2.carbon.cep.wihidum.loadbalancer.admin.internal.util;

import org.wso2.carbon.cep.wihidum.loadbalancer.LoadBalancerServiceInterface;

/**
 * This class is used to access LoadBalancerService Interface from the Admin Class.
 */
public class LoadbalancerAdminValueHolder {

    private LoadBalancerServiceInterface loadbalancerService;

    private static LoadbalancerAdminValueHolder instance = new LoadbalancerAdminValueHolder();

    private LoadbalancerAdminValueHolder(){

    }

    public static LoadbalancerAdminValueHolder getInstance(){
        return instance;
    }

    public LoadBalancerServiceInterface getLoadbalancerService(){
        return this.loadbalancerService;
    }

    public void registerLoadBalancerService(LoadBalancerServiceInterface lbService){
        this.loadbalancerService = lbService;
    }

    public void unRegisterLoadBalancerService(LoadBalancerServiceInterface lbService){
        this.loadbalancerService = null;
    }












}
