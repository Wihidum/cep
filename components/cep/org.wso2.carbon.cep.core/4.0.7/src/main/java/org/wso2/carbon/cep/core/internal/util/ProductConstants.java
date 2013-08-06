package org.wso2.carbon.cep.core.internal.util;/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public static final String CLIENT_TRUST_STORE_PATH = "/src/main/resources/keystores/client-truststore.jks";

}
