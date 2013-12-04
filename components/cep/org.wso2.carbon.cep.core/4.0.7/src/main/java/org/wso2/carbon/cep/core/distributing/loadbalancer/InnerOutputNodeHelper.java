package org.wso2.carbon.cep.core.distributing.loadbalancer;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.cep.core.internal.util.CEPConstants;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;

public class InnerOutputNodeHelper {


    public static OMElement toOM(InnerOutputNode innerOutputNode){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement outputNode = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_INNER_OUTPUT,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        OMElement id = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_LB_ID,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        id.setText(innerOutputNode.getId());
        OMElement type = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_ATTR_TYPE,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        type.setText(innerOutputNode.getType());
        outputNode.addChild(id);
        outputNode.addChild(type);
         List<LBOutputNode> lbOutputNodeList = innerOutputNode.getLbOutputNodeList();
        for(LBOutputNode lbOutputNode: lbOutputNodeList){
            OMElement out = LBOutputNodeHelper.toOM(lbOutputNode);
            outputNode.addChild(out);
        }
        return outputNode;
    }


    public static  InnerOutputNode fromOM(OMElement outputElement){
        InnerOutputNode innerOutputNode = new InnerOutputNode();
        OMElement id =
                outputElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_LB_ID));
        if (id != null) {
            innerOutputNode.setId(id.getText().trim());
        }
        OMElement type =
                outputElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_ATTR_TYPE));
        if (type != null) {
            innerOutputNode.setType(type.getText().trim());
        }
        for (Iterator iterator =  outputElement.getChildrenWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_OUTPUTNODE)); iterator.hasNext(); ) {
            LBOutputNode outputNode = LBOutputNodeHelper.fromOM((OMElement) iterator.next());
            innerOutputNode.addLbOutputNode(outputNode);

        }
        return innerOutputNode;
    }



}
