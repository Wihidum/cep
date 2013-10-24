package org.wso2.carbon.cep.wihidum.loadbalancer.persistance;






import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.dom.DOOMAbstractFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfigHelper;
import org.wso2.carbon.cep.wihidum.loadbalancer.conf.LoadBalancerConfiguration;
import org.wso2.carbon.cep.wihidum.loadbalancer.internal.util.LoadBalancerConstants;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class LBResourcePersistance {
    private static Logger log = Logger.getLogger(LBResourcePersistance.class);

 public static void save(LoadBalancerConfiguration loadBalancerConfiguration){
     String carbonHome = System.getProperty(ServerConstants.CARBON_CONFIG_DIR_PATH);
     String path = carbonHome + File.separator + LoadBalancerConstants.WIHIDUM_DIR + File.separator + LoadBalancerConstants.LOADBALANCER_FILE;
     File configFile = new File(path);
     if (!configFile.exists()) {
         log.info("The " + LoadBalancerConstants.WIHIDUM_DIR + File.separator + LoadBalancerConstants.LOADBALANCER_FILE + " can not found ");
         return ;
     }
     OMElement configOM = LoadBalancerConfigHelper.toOM(loadBalancerConfiguration);
     XMLOutputFactory xof = XMLOutputFactory.newInstance();
     XMLStreamWriter writer = null;
     try {
             writer = xof.
                     createXMLStreamWriter(new FileWriter(path));

         OMFactory doomFactory = DOOMAbstractFactory.getOMFactory();
         StAXOMBuilder doomBuilder = new StAXOMBuilder(doomFactory,
                 configOM.getXMLStreamReader());
         Document doc = (Document) doomBuilder.getDocument();
         OutputFormat format = new OutputFormat(doc);
         format.setIndenting(true);
         format.setIndent(2);
         XMLSerializer serializer = new XMLSerializer(new FileOutputStream(path), format);
         serializer.serialize(doc);


//         configOM.serialize(writer);
//         writer.flush();
         log.info("Successfully Saved LoadBalancer Configuration");

     } catch (XMLStreamException e) {
        log.error(e.getMessage());
     }
     catch (IOException e) {
         log.error(e.getMessage());
     }


 }








}
