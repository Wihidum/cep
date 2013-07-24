package org.wso2.carbon.cep.wihidum.loadbalancer.internal;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.LoadBalancerConfigException;
import org.wso2.carbon.databridge.core.exception.DataBridgeException;


/**
 * @scr.component name="loadbalancer.component" immediate="true"
 */
public class LoadBalancerDS {

    private static Logger log = Logger.getLogger(LoadBalancerDS.class);
    private ServiceRegistration serviceRegistration;

    /**
     * initialize the loadbalancer here.
     *
     * @param context
     */
    protected void activate(ComponentContext context) throws LoadBalancerConfigException, DataBridgeException {
        serviceRegistration = context.getBundleContext().registerService(LoadBalancerDS.class.getName(), LoadBalancerDS.class, null);


    }

    protected void deactivate(ComponentContext context) {
        context.getBundleContext().ungetService(serviceRegistration.getReference());
    }


}
