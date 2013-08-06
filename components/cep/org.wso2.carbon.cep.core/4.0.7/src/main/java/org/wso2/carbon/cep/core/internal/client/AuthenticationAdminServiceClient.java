package org.wso2.carbon.cep.core.internal.client;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.cep.core.internal.util.ProductConstants;

public class AuthenticationAdminServiceClient {
     private static AuthenticationAdminStub authenticationAdminStub;

    public static void init(String backEndServerURL) throws AxisFault {

        authenticationAdminStub = new AuthenticationAdminStub(backEndServerURL);
        ServiceClient client = authenticationAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
    }

    public static String login( String hostName, String userName, String password) throws Exception {

        authenticationAdminStub.login(userName, password, hostName);
        ServiceContext serviceContext = authenticationAdminStub.
                _getServiceClient().getLastOperationContext().getServiceContext();
        String sessionCookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
        return sessionCookie;
    }

    public static void setSystemProperties(String keyStorePath,String keyStoreType, String keyStorePassword ){

        System.setProperty(ProductConstants.TRUSTSTORE, keyStorePath);
        System.setProperty(ProductConstants.TRUSTSTORE_PASSWORD, keyStorePassword);
        System.setProperty(ProductConstants.TRUSTSTORE_TYPE, keyStoreType);

    }




}
