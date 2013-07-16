package org.wso2.carbon.cep.wihidum.loadbalancer.internal;


import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.eventreceiver.ExternalEventReceiver;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.exception.LoadBalancerConfigException;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConfBuilder;
import org.wso2.carbon.databridge.receiver.thrift.export.DataReceiverExporter;


public class LoadBalancerDS {

private static Logger log= Logger.getLogger(LoadBalancerDS.class);

protected void activate(ComponentContext context)  {
    try {
      OMElement omElement =  LoadBalancerConfBuilder.loadConfigXML();
      LoadBalancerConfiguration loadBalancerConfiguration =  LoadBalancerConfBuilder.fromOM(omElement);
      if(loadBalancerConfiguration.isLoadbalanceron()){
          DataReceiverExporter.thriftDataReceiver.stop();
          ExternalEventReceiver.startReciver("localhost",loadBalancerConfiguration.getPort());
      }

    } catch (LoadBalancerConfigException e) {
       e.printStackTrace();
    }


}

protected void deactivate(ComponentContext context){


}
}
