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

        OMElement type = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_ATTR_TYPE,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        type.setText(loadbalancer.getIp());
        loadbalancerOM.addChild(type);

        List<LBOutputNode> lbOutputNodes = loadbalancer.getOutputNodeList();
        if (lbOutputNodes.size() > 0){
            for (LBOutputNode lbOutputNode : lbOutputNodes){
                OMElement element = LBOutputNodeHelper.toOM(lbOutputNode);
                loadbalancerOM.addChild(element);
            }
        }
        List<InnerOutputNode> innerOutputNodeList = loadbalancer.getInnerOutputNodeList();
        if (innerOutputNodeList .size() > 0){
            for (InnerOutputNode innerOutputNode : innerOutputNodeList){
                OMElement element = InnerOutputNodeHelper.toOM(innerOutputNode);
                loadbalancerOM.addChild(element);
            }
        }
        List<Stream> streamList = loadbalancer.getStreamList();
        if (streamList  .size() > 0){
            for (Stream stream : streamList){
                OMElement element = StreamHelper.toOM(stream);
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
        OMElement type =
                lbElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_ATTR_TYPE));
        if (type != null) {
            loadbalancer.setType(type.getText().trim());
        }

        OMElement outputOmElement = null;

        for (Iterator iterator = lbElement.getChildrenWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_OUTPUTNODE)); iterator.hasNext(); ) {
            outputOmElement = (OMElement) iterator.next();
            LBOutputNode outputNode = LBOutputNodeHelper.fromOM(outputOmElement);

            loadbalancer.addOutputNode(outputNode);

        }

        OMElement streamOmElement = null;

        for (Iterator iterator = lbElement.getChildrenWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_ATTR_STREAM)); iterator.hasNext(); ) {
            streamOmElement = (OMElement) iterator.next();
            Stream stream = StreamHelper.fromOM(streamOmElement);
            loadbalancer.addStream(stream);
        }
        OMElement innerOmElement = null;

        for (Iterator iterator = lbElement.getChildrenWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_INNER_OUTPUT)); iterator.hasNext(); ) {
            innerOmElement = (OMElement) iterator.next();
            InnerOutputNode   innerOutputNode = InnerOutputNodeHelper.fromOM(innerOmElement);

            loadbalancer.addInnerOutputNode(innerOutputNode);

        }

        return loadbalancer;
    }

}
