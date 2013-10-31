package org.wso2.carbon.cep.core.internal.client;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.cep.core.internal.util.ProductConstants;

import java.rmi.RemoteException;


public class AuthenticationAdminServiceClient {

    private static Logger logger = Logger.getLogger(AuthenticationAdminServiceClient.class);
     private static AuthenticationAdminStub authenticationAdminStub;

    public static void init(String backEndServerURL)  {

        try {
            authenticationAdminStub = new AuthenticationAdminStub(backEndServerURL);
        } catch (Exception e) {
             logger.error(e.getMessage());
        }
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


    public static void logout() throws RemoteException, LogoutAuthenticationExceptionException {

        authenticationAdminStub.logout();

    }








    public static void setSystemProperties(String keyStorePath,String keyStoreType, String keyStorePassword ){

        System.setProperty(ProductConstants.TRUSTSTORE, keyStorePath);
        System.setProperty(ProductConstants.TRUSTSTORE_PASSWORD, keyStorePassword);
        System.setProperty(ProductConstants.TRUSTSTORE_TYPE, keyStoreType);

    }




}
