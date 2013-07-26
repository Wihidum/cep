package org.wso2.carbon.cep.wihidum.loadbalancer.utils;

import java.io.File;
import java.io.IOException;


public class KeyStoreUtil {
    static File filePath = new File(".");

    public static void setTrustStoreParams() {
        String trustStore = null;
        try {
            trustStore = filePath.getCanonicalPath() + "/src/main/resources";
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperty("javax.net.ssl.trustStore", trustStore + "/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

    }

    public static void setKeyStoreParams() {
        String keyStore = null;
        try {
            keyStore = filePath.getCanonicalPath() + "/src/main/resources";
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperty("Security.KeyStore.Location", keyStore + "/wso2carbon.jks");
        System.setProperty("Security.KeyStore.Password", "wso2carbon");

    }
}
