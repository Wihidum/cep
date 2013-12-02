package org.wso2.carbon.cep.core.distributing.loadbalancer;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.cep.core.internal.util.CEPConstants;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;

public class StreamHelper {

    public static OMElement toOM(Stream stream){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement streamOM = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_ATTR_STREAM,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        OMElement id = factory.createOMElement(new QName(
                CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_LB_ID,
                CEPConstants.CEP_CONF_CEP_NAME_SPACE_PREFIX));
        id.setText(stream.getId());
        streamOM.addChild(id);
        List<InnerOutputNode> innerOutputNodeList = stream.getInnerOutputNodeList();
        for(InnerOutputNode innerOutputNode: innerOutputNodeList){
            OMElement innerOM = InnerOutputNodeHelper.toOM(innerOutputNode);
           streamOM.addChild(innerOM);
        }
        return streamOM;
    }


    public static  Stream fromOM(OMElement outputElement){
        Stream stream = new Stream();
        OMElement id =
                outputElement.getFirstChildWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                        CEPConstants.CEP_CONF_LB_ID));
        if (id != null) {
            stream.setId(id.getText().trim());
        }

        for (Iterator iterator =  outputElement.getChildrenWithName(new QName(CEPConstants.CEP_CONF_NAMESPACE,
                CEPConstants.CEP_CONF_INNER_OUTPUT)); iterator.hasNext(); ) {
            InnerOutputNode outputNode = InnerOutputNodeHelper.fromOM((OMElement) iterator.next());
            stream.addInnerOutputNode(outputNode);

        }
        return stream;
    }




}
