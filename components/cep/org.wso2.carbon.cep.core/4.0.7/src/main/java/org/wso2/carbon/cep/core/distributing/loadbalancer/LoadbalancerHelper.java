package org.wso2.carbon.cep.core.distributing.loadbalancer;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.internal.config.QueryHelper;
import org.wso2.carbon.cep.core.internal.util.CEPConstants;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;

public class LoadbalancerHelper {

    public static OMElement getOM(Loadbalancer loadbalancer) {

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement loadbalancerOM = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_LOADBALANCER,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        OMElement ip = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_QUERY_IP,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        ip.setText(loadbalancer.getIp());
        loadbalancerOM.addChild(ip);

        List<LBOutputNode> lbOutputNodes = loadbalancer.getOutputNodeList();
        if (lbOutputNodes.size() > 0) {
            for (LBOutputNode lbOutputNode : lbOutputNodes) {
                OMElement element = LBOutputNodeHelper.toOM(lbOutputNode);
                loadbalancerOM.addChild(element);
            }
        }
        return loadbalancerOM;
    }


    public static Loadbalancer fromOM(OMElement lbElement) {

        Loadbalancer loadbalancer = new Loadbalancer();

        OMElement ip =
                lbElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_QUERY_IP));
        if (ip != null) {
            loadbalancer.setIp(ip.getText().trim());
        }

        OMElement outputOmElement = null;

        for (Iterator iterator = lbElement.getChildrenWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_OUTPUTNODE)); iterator.hasNext(); ) {
            outputOmElement = (OMElement) iterator.next();
            LBOutputNode outputNode = LBOutputNodeHelper.fromOM(outputOmElement);

            loadbalancer.addOutputNode(outputNode);

        }
        return loadbalancer;
    }

}
