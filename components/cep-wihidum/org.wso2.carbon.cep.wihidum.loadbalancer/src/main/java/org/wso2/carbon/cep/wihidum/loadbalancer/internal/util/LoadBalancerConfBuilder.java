package org.wso2.carbon.cep.wihidum.loadbalancer.internal.util;


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.log4j.Logger;
import org.wso2.carbon.cep.wihidum.loadbalancer.exception.LoadBalancerConfigException;
import org.wso2.carbon.utils.ServerConstants;


import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;


public class LoadBalancerConfBuilder {

    private static Logger log = Logger.getLogger(LoadBalancerConfBuilder.class);
    static File filePath = new File(".");

    public static OMElement loadConfigXML() throws LoadBalancerConfigException {
        String carbonHome = System.getProperty(ServerConstants.CARBON_CONFIG_DIR_PATH);
        String path = null;
        try {
            path = filePath.getCanonicalPath() + "/src/main/resources/" + LoadBalancerConstants.LOADBALANCER_FILE;
            System.out.println("canonical path = " + path);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//                carbonHome + File.separator + LoadBalancerConstants.WIHIDUM_DIR + File.separator + LoadBalancerConstants.LOADBALANCER_FILE;
        File configFile = new File(path);
        if (!configFile.exists()) {
            log.info("The " + LoadBalancerConstants.WIHIDUM_DIR + File.separator + LoadBalancerConstants.LOADBALANCER_FILE + " can not found ");
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
        } catch (FileNotFoundException e) {
            throw new LoadBalancerConfigException("loadbalancer-conf.xml"
                    + "cannot be found in the path : " + path, e);
        } catch (XMLStreamException e) {
            throw new LoadBalancerConfigException("Invalid XML for " + "loadbalancer-conf.xml"
                    + " located in the path : " + path, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ingored) {
                log.info("Can not close the input stream");
                throw new LoadBalancerConfigException("Can not close the input stream");
            }
        }
    }


}
