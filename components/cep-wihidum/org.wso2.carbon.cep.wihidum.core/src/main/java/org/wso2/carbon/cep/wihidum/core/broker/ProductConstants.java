package org.wso2.carbon.cep.wihidum.core.broker;


import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

public class ProductConstants {
    public static final String HTTPS_PORT = "9443";
    public static final String USER_NAME = "admin";
    public static final String PASSWORD = "admin";
    public static final String KEY_STORE_PASSWORD = "wso2carbon";
    public static final String KEY_STORE_TYPE = "jks";
    public static final String TRUSTSTORE= "javax.net.ssl.trustStore";
    public static final String TRUSTSTORE_PASSWORD= "javax.net.ssl.trustStorePassword";
    public static final String TRUSTSTORE_TYPE = "javax.net.ssl.trustStoreType";

    public static final String CLIENT_TRUST_STORE_PATH =  System.getProperty(ServerConstants.CARBON_HOME)+ File.separator+"repository"+File.separator+"resources"+File.separator+"security"+File.separator+"client-truststore.jks";

}
