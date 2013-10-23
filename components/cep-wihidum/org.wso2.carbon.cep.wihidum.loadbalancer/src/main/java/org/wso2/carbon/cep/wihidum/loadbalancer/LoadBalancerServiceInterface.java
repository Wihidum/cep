package org.wso2.carbon.cep.wihidum.loadbalancer;


import org.apache.axis2.engine.AxisConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;

public interface LoadBalancerServiceInterface {

public void addLoadbalancerConfiguration(LoadBalancerConfiguration loadBalancerConfiguration,AxisConfiguration axisConfiguration);

}
