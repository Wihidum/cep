package org.wso2.carbon.cep.admin.internal.util;


import org.wso2.carbon.utils.ServerConstants;

public interface ProductConstants {
    // public static final String CLIENT_TRUST_STORE_PATH = "/media/New Volume/FYP/GIT/siddhi-lb/cep/org.wso2.carbon.cep.core/4.0.7/src/main/resources/keystores/client-truststore.jks";
    public static final String HTTPS_PORT = "9443";
    public static final String USER_NAME = "admin";
    public static final String PASSWORD = "admin";
    public static final String KEY_STORE_PASSWORD = "wso2carbon";
    public static final String KEY_STORE_TYPE = "jks";
    public static final String TRUSTSTORE= "javax.net.ssl.trustStore";
    public static final String TRUSTSTORE_PASSWORD= "javax.net.ssl.trustStorePassword";
    public static final String TRUSTSTORE_TYPE = "javax.net.ssl.trustStoreType";

    public static final String CLIENT_TRUST_STORE_PATH =  System.getProperty(ServerConstants.CARBON_HOME)+"/repository/resources/security/client-truststore.jks";
    public  String DISTRIBUTED_PROCESSING = "siddhi.enable.distributed.processing";


}
