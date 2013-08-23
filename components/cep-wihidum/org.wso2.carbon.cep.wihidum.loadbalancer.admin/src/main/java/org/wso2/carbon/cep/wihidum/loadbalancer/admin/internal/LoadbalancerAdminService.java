package org.wso2.carbon.cep.wihidum.loadbalancer.admin.internal;


import org.wso2.carbon.cep.wihidum.loadbalancer.LoadBalancerServiceInterface;
import org.wso2.carbon.cep.wihidum.loadbalancer.admin.internal.util.LoadbalancerAdminValueHolder;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.core.AbstractAdmin;

public class LoadbalancerAdminService extends AbstractAdmin {
   public void addLoadBalancerConfiguration(LoadbalancerDTO loadbalancerDTO){
      LoadBalancerConfiguration loadBalancerConfiguration = LoadbalacerUtils.adaptLoadbalancerConfiguration(loadbalancerDTO);
      LoadBalancerServiceInterface loadBalancerServiceInterface = LoadbalancerAdminValueHolder.getInstance().getLoadbalancerService();
      loadBalancerServiceInterface.addLoadbalancerConfiguration(loadBalancerConfiguration,getAxisConfig());
   }

}
