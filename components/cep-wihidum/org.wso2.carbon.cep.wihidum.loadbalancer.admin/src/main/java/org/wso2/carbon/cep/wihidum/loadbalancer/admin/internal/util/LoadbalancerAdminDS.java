package org.wso2.carbon.cep.wihidum.loadbalancer.admin.internal.util;


import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.cep.wihidum.loadbalancer.LoadBalancerServiceInterface;



/**
 * this class is used to get the LoadBalancerService service. it is used to send the
 * requests received from the Admin service to real loadbalancer
 * @scr.component name="loadbalaceradmin.component" immediate="true"
 * @scr.reference name="loadbalancer.service"
 * interface="org.wso2.carbon.cep.wihidum.loadbalancer.LoadBalancerServiceInterface" cardinality="1..1"
 * policy="dynamic" bind="setLoadBalancerService" unbind="unSetLoadBalancerService"
 */


public class LoadbalancerAdminDS {

    protected void activate(ComponentContext context) {


    }

    protected void setLoadBalancerService(LoadBalancerServiceInterface loadbalancerService) {
        LoadbalancerAdminValueHolder.getInstance().registerLoadBalancerService(loadbalancerService);
    }

    protected void unSetLoadBalancerService(LoadBalancerServiceInterface loadbalancerService) {
       LoadbalancerAdminValueHolder.getInstance().unRegisterLoadBalancerService(loadbalancerService);
    }













}
