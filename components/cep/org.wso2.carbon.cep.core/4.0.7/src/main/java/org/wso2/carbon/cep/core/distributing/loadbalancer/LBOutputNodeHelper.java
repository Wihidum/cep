package org.wso2.carbon.cep.core.distributing.loadbalancer;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.internal.config.QueryHelper;
import org.wso2.carbon.cep.core.internal.util.CEPConstants;

import javax.xml.namespace.QName;
import java.util.Iterator;

public class LBOutputNodeHelper {


   public static OMElement toOM(LBOutputNode lbOutputNode){
       OMFactory factory = OMAbstractFactory.getOMFactory();
       OMElement outputNode = factory.createOMElement(new QName(
               CEPConstants.CEP_CONF_NAMESPACE,
               CEPConstants.CEP_CONF_OUTPUTNODE,
               CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
       OMElement ip = factory.createOMElement(new QName(
               CEPConstants.CEP_CONF_NAMESPACE,
               CEPConstants.CEP_CONF_QUERY_IP,
               CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
       ip.setText(lbOutputNode.getIp());
       OMElement port = factory.createOMElement(new QName(
               CEPConstants.CEP_CONF_NAMESPACE,
               CEPConstants.CEP_CONF_PORT,
               CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
       port.setText(lbOutputNode.getPort());

       if(lbOutputNode.getId()!= null){
           OMElement id = factory.createOMElement(new QName(
                   CEPConstants.CEP_CONF_NAMESPACE,
                   CEPConstants.CEP_CONF_LB_ID,
                   CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
           id.setText(lbOutputNode.getId());
           outputNode.addChild(id);
       }
       outputNode.addChild(ip);
       outputNode.addChild(port);
       return outputNode;
   }


    public static  LBOutputNode fromOM(OMElement outputElement){
        LBOutputNode lbOutputNode = new LBOutputNode();
        OMElement ip =
                outputElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_QUERY_IP));
        if (ip != null) {
            lbOutputNode.setIp(ip.getText().trim());
        }
        OMElement port =
                outputElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_PORT));
        if (port != null) {
            lbOutputNode.setPort(port.getText().trim());
        }
        OMElement id =
                outputElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_LB_ID));
        if (id != null) {
            lbOutputNode.setPort(id.getText().trim());
        }
          return lbOutputNode;
    }






}
