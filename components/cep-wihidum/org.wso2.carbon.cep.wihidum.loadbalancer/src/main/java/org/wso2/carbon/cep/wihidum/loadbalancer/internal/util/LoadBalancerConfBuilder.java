package org.wso2.carbon.cep.wihidum.loadbalancer.internal.util;


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.exception.LoadBalancerConfigException;
import org.wso2.carbon.utils.ServerConstants;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Iterator;

public class LoadBalancerConfBuilder {

    private static Logger log =  Logger.getLogger(LoadBalancerConfBuilder.class);


    public static OMElement loadConfigXML() throws  LoadBalancerConfigException {
        String carbonHome = System.getProperty(ServerConstants.CARBON_CONFIG_DIR_PATH);
        String path = carbonHome + File.separator + LoadBalancerConstants.LOADBALANCER_DIR+File.separator+LoadBalancerConstants.LOADBALANCER_FILE;
        File configFile = new File(path);
        if (!configFile.exists()){
            log.info("The " + LoadBalancerConstants.LOADBALANCER_DIR+File.separator+LoadBalancerConstants.LOADBALANCER_FILE + " can not found ");
            return null;
        }
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(configFile));
            XMLStreamReader parser = XMLInputFactory.newInstance().
                    createXMLStreamReader(inputStream);
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement omElement = builder.getDocumentElement();
            omElement.build();
            return omElement;
        }catch (FileNotFoundException e){
            throw new LoadBalancerConfigException("wihidum-conf.xml"
                    + "cannot be found in the path : " + path, e);
        } catch (XMLStreamException e) {
            throw new LoadBalancerConfigException("Invalid XML for " + "wihidum-conf.xml"
                    + " located in the path : " + path, e);
        }finally{
            try{
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (IOException ingored){
                throw new  LoadBalancerConfigException("Can not close the input stream");
            }
        }
    }


    public static LoadBalancerConfiguration fromOM(OMElement omElement){
        LoadBalancerConfiguration loadBalancerConfiguration = new LoadBalancerConfiguration();;
        Iterator iterator =omElement.getChildElements();
        for(;iterator.hasNext();){
            OMElement omElementChild = (OMElement)iterator.next();
            if(omElementChild.getLocalName().equals("loadbalancer")){
                if(omElementChild.getText().equals("true")){
                    loadBalancerConfiguration.setLoadbalanceron(true);
                }else{
                    loadBalancerConfiguration.setLoadbalanceron(false);
                }
            } else if(omElementChild.getLocalName().equals("port")){
                loadBalancerConfiguration.setPort(Integer.parseInt(omElementChild.getText().trim()));
            }
        }
        return  loadBalancerConfiguration;
    }



}
