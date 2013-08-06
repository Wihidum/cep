package org.wso2.carbon.cep.wihidum.core.cluster;



public class PropertyGenerator {
    private String inetAddress;
    private int receiverPort = Constants.DEFAULT_RECEIVER_PORT;
    private int authenticatorPort = Constants.DEFAULT_AUTHENTICATOR_PORT;
    public PropertyGenerator(String Address) {
        inetAddress = Address;
    }

    public String getReceiverURL() {
        return "tcp://"+inetAddress+":"+receiverPort;
    }

    public String getAuthenticatorURL() {
        return "ssl://"+inetAddress+":"+authenticatorPort;
    }

    public String getUsername() {
        return Constants.DEFAULT_USERNAME;
    }

    public String getPassword() {
        return Constants.DEFAULT_PASSWORD;
    }
}
